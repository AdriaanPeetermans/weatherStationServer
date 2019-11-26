package servers;

import java.util.ArrayList;

import org.java_websocket.WebSocket;

import game.GameEngine;
import helpers.Server;
import helpers.SocketMessage;
import helpers.playerData.PlayerColors;
import helpers.playerData.PlayerData;
import helpers.playerData.Segment;

public class GameServer extends Server {
	
	public GameServer(int port, DistributerServer mainServer) {
		super(port);
		this.mainServer = mainServer;
	}
	
	private final DistributerServer mainServer;
	
	private GameEngine game;
	
	public boolean isHost = false;
	
	public int playerIndex;
	
	private String word = "nikske";
	
	/**
	 * Message:
	 * 		functionName + # + param0 + # + param1 + # + ...
	 * 
	 * Functions:
	 * 		name:			joinGame 		(joins to game)
	 * 		parameters:		code string
	 * 		gameState:		not existing
	 * 		returns:		1 (game exists) or 0 (game does not exist) or -1 (wrong code)
	 * 
	 * 		name:			checkCode 		(only checks code. Does not join)
	 * 		parameters:		code string
	 * 		gameState:		not existing
	 * 		returns:		1 (game exists) or 0 (game does not exist) or -1 (wrong code)
	 * 
	 * 		name:			hostGame		(starts a new game as host)
	 * 		parameters:		none
	 * 		gameState:		not existing
	 * 		returns:		"code#" + gameCode
	 * 
	 * 		name:			getColors		(get player colors, without "#")
	 * 		parameters:		none
	 * 		gameState:		0
	 * 		returns:		"colors#" + two colors in hexadecimal format, separated by "#"
	 * 
	 * 		name:			addPlayer		(receive player name and drawing)
	 * 		parameters:		name
	 * 						segment1%segment2%segment3% ..., segment: color&size&length&x0&y0&x1&y1& ...
	 * 		gameState		0
	 * 		returns:		1 if player is VIP, 0 otherwise
	 * 
	 * 		name:			startGame
	 * 		parameters:		none
	 * 		gameState:		0
	 * 		returns:		"players#" + number of players
	 * 
	 * 		name:			getWord
	 * 		parameters:		none
	 * 		gameState:		1
	 * 		returns:		"word#" + word + "#" + time in seconds
	 * 
	 * 		name:			sendDrawing
	 * 		parameters:		segment1%segment2%segment3% ..., segment: color&size&length&x0&y0&x1&y1& ...
	 * 		gameState:		1
	 * 		returns: 		nothing
	 * 
	 * 		name:			getWordTime
	 * 		parameters:		none
	 * 		gameSTate:		2
	 * 		returns:		"wordTime#" + seconds for word time integer if this is not the current player's drawing,
	 * 						"waitLonger" if this is the player's own drawing
	 * 
	 * 		name:			sendWord
	 * 		parameters:		word
	 * 		gameState:		2
	 * 		returns:		nothing
	 * 
	 * 		name:			getWords
	 * 		parameters:		none
	 * 		gameState:		3
	 * 		returns:		"words#" + numberWords + "#" + word1 + "#" + word2 + "#" + ...
	 * 
	 * 		name:			goFurther
	 * 		parameters:		gameState integer: 1: waiting for players drawing
	 * 		gameState:		?
	 * 		returns:		"endWait" if yes, "no" if no
	 * 
	 * 		name:			getColorsHost
	 * 		parameters:		none
	 * 		gameState:		1
	 * 		returns:		"playerColors#" + color00#color01#color10#color11#color20# ... #color70#color71
	 * 
	 * 		name:			getPlayerNames
	 * 		parameters:		none
	 * 		gameState:		1
	 * 		returns:		playerNames#numberPlayers#player1Name# ...
	 * 
	 * 		name:			getDrawing
	 * 		parameters:		drawingNumber
	 * 		gameState:		2
	 * 		returns:		wordChooseTime#segment1%segment2%segment3% ..., segment: color&size&length&x0&y0&x1&y1& ...
	 * 
	 * 		name:			getVoteTime
	 * 		parameters:		none
	 * 		gameState:		3
	 * 		returns:		voteTime
	 * 
	 * 		name:			voted
	 * 		parameter:		word
	 * 		gameState:		3
	 * 		returns:		nothing
	 */
	@Override
	public void handleMessage(SocketMessage t) {
		String[] parts = t.message.split("#");
		System.out.println(this.playerIndex);
		System.out.println("Received message: ".concat(t.message));
		switch (parts[0]) {
			case "joinGame":
				t.sock.send(Integer.toString(this.joinGame(parts[1])));
				break;
			case "checkCode":
				t.sock.send(Integer.toString(this.checkCode(parts[1])));
				break;
			case "hostGame":
				t.sock.send(this.hostGame());
				break;
			case "getColors":
				t.sock.send(this.getColors());
				break;
			case "addPlayer":
				t.sock.send(Integer.toString(this.addPlayer(parts[1], parts[2])));
				break;
			case "startGame":
				t.sock.send(Integer.toString(this.startGame()));
				break;
			case "getWord":
				t.sock.send("word#".concat(this.word).concat("#").concat(Integer.toString(this.game.wordTime)));
				break;
			case "sendDrawing":
				this.sendDrawing(parts[1]);
				break;
			case "getWordTime":
				t.sock.send(this.getWordTimeMessage());
				//t.sock.send("wordTime#".concat(Integer.toString(this.getWordTime())));
				break;
			case "sendWord":
				this.sendWord(parts[1]);
				break;
			case "getWords":
				t.sock.send("words#".concat(this.getWords()));
				break;
			case "goFurther":
				t.sock.send(this.goFurther(Integer.parseInt(parts[1])));
				break;
			case "getPlayerNames":
				t.sock.send(this.getPlayerNames());
				break;
			case "getColorsHost":
				t.sock.send(this.getColorsHost());
				break;
			case "getDrawing":
				t.sock.send(this.getDrawing(Integer.parseInt(parts[1])));
				break;
			case "getVoteTime":
				t.sock.send("voteTime#".concat(Integer.toString(this.game.voteTime)));
				break;
			case "voted":
				this.voted(parts[1]);
				break;
		}
	}
	
	@Override
	public void handleClose(SocketMessage t) {
		
	}
	
	private int joinGame(String code) {
		if (code.length() != 6) {
			return -1;
		}
		GameEngine engine = this.mainServer.getEngine(code);
		if (engine != null) {
			this.game = engine;
			this.playerIndex = this.game.addPlayer(this);
			return 1;
		}
		return 0;
	}
	
	private int checkCode(String code) {
		if (code.length() != 6) {
			return -1;
		}
		if (this.mainServer.existCode(code)) {
			return 1;
		}
		return 0;
	}
	
	private String hostGame() {
		this.isHost = true;
		this.game = this.mainServer.addGame(this);
		return "code#".concat(this.game.code);
	}
	
	private String getColors() {
		PlayerColors colors = this.mainServer.playerColors.get(this.playerIndex);
		return "colors#".concat(colors.color1).concat("#").concat(colors.color2);
	}
	
	private int addPlayer(String name, String drawing) {
		ArrayList<Segment> segs = Segment.parseSegment(drawing);
		PlayerData playerData = new PlayerData(name, segs, this.mainServer.playerColors.get(this.playerIndex), this.playerIndex);
		this.game.addPlayerDrawing(playerData, this.playerIndex);
		if (this.playerIndex == 0) {
			return 1;
		}
		else {
			return 0;
		}
	}
	
	private int startGame() {
		return this.game.startGame();
	}
	
	private void sendDrawing(String drawing) {
		this.game.addDrawing(Segment.parseSegment(drawing), this.playerIndex);
	}
	
	private int getWordTime() {
		return this.game.wordChooseTime;
	}
	
	private String getWordTimeMessage() {
		if (this.game.getCurrentPlayer() == this.playerIndex) {
			return "waitLonger";
		}
		else {
			return "wordTime#".concat(Integer.toString(this.getWordTime()));
		}
	}
	
	private void sendWord(String word) {
		this.game.addWord(word, this.playerIndex);
	}
	
	private String getWords() {
//		if (this.playerIndex == this.game.getCurrentPlayer()) {
//			//This should not happen!
//			String result = "No";
//			return result;
//		}
		ArrayList<String> words = this.game.getWords(this.playerIndex, this.isHost);
		String result = Integer.toString(words.size()).concat("#");
		for (int i = 0; i < words.size(); i++) {
			result = result.concat(words.get(i)).concat("#");
		}
		return result;
	}
	
	private String goFurther(int state) {
		System.out.println("Received go further: ".concat(Integer.toString(state)).concat(Integer.toString(this.game.state)));
		if (this.playerIndex == this.game.getCurrentPlayer()) {
			//Current player should wait as his drawing is displayed on the screen.
			return "no";
		}
		if (state < this.game.state) {
			return "endWait";
		}
		else {
			return "no";
		}
	}
	
	private String getColorsHost() {
		String result = "playerColors#";
		result = result.concat(Integer.toString(this.game.getNumberPlayers())).concat("#");
		result = result.concat(Integer.toString(this.mainServer.playerColors.size())).concat("#");
		for (PlayerColors color : this.mainServer.playerColors) {
			result = result.concat(color.color1).concat("#").concat(color.color2).concat("#");
		}
		return result;
	}
	
	private String getPlayerNames() {
		String result = "playerNames#";
		result = result.concat(Integer.toString(this.game.getNumberPlayers())).concat("#");
		for (int i = 0; i < this.game.getNumberPlayers(); i++) {
			result = result.concat(this.game.playerDatas.get(i).name).concat("#");
		}
		return result;
	}
	
	private String getDrawing(int drawingNumber) {
		String result = "drawing#";
		result = result.concat(Integer.toString(this.game.getNumberPlayers())).concat("#");
		result = result.concat(Integer.toString(this.game.wordChooseTime)).concat("#");
		ArrayList<Segment> drawing = this.game.getRandomDrawing(drawingNumber);
		for (Segment segment : drawing) {
			result = result.concat(segment.toString()).concat("%");
		}
		return result;
	}
	
	private void voted(String word) {
		this.game.voted(this.playerIndex, word);
	}
	
	/**
	 * Notify this gameServer for an event (add player, ...).
	 * 
	 * @param reason:
	 * 		newPlayer:		Notify for player which is still drawing its avatar
	 * 		gameState		0
	 * 
	 * 		addPlayer:		Send player data to host
	 * 		gameState:		0
	 * 
	 * 		startGame:		Notify player that game has started
	 * 		gameState:		1
	 * 
	 * 		startGameHost:	Notify host that game has started
	 * 		gameState:		1
	 * 
	 * 		drawingReady:	Notify host that player is ready drawing
	 * 		gameState:		1
	 * 
	 * 		endWait:		Notify player to stop waiting for other players
	 * 		gameState:		1
	 * 
	 * 		allDrawings:	Notify host that all players are ready drawing
	 * 		gameState:		2
	 * 
	 * 		voteWord:		Notify host that all players are ready for word voting
	 * 		gameState:		3
	 * 
	 * 		showPoints:		Notify host that all players have voted and points can be showed
	 * 		gameState:		2
	 */
	public void notify(String reason, Object params) {
		System.out.println(this.playerIndex);
		System.out.println("Notify: ".concat(reason));
		switch (reason) {
			case "newPlayer":
				int playerIndex = (int) params;
				this.newPlayer(playerIndex);
				break;
			case "addPlayer":
				PlayerData playerData = (PlayerData) params;
				this.addPlayer(playerData);
				break;
			case "startGame":
				String word = (String) params;
				this.word = word;
				this.playerStart();
				break;
			case "startGameHost":
				this.playerStart();
				break;
			case "drawingReady":
				int playerIndecs = (int) params;
				this.drawingReady(playerIndecs);
				break;
			case "endWait":
				this.endWait();
				break;
			case "allDrawings":
				this.allDrawingsReady();
				break;
			case "voteWord":
				this.voteWord();
				break;
			case "showPoints":
				this.showPoints();
				break;
		}
	}
	
	private void newPlayer(int playerIndex) {
		System.out.println("komt hier: ".concat(Integer.toString(playerIndex)));
		String message = "newPlayer#".concat(Integer.toString(playerIndex));
		((WebSocket) this.webSocketServer.connections().toArray()[0]).send(message);
		System.out.println(message);
	}
	
	private void addPlayer(PlayerData playerData) {
		String message = "addPlayer#".concat(Integer.toString(playerData.index)).concat("#").concat(playerData.toString());
		((WebSocket) this.webSocketServer.connections().toArray()[0]).send(message);
	}
	
	private void playerStart() {
		String message = "startGame#".concat(Integer.toString(this.game.wordTime));
		((WebSocket) this.webSocketServer.connections().toArray()[0]).send(message);
	}
	
	private void drawingReady(int playerIndex) {
		String message = "readyDrawing#".concat(Integer.toString(playerIndex));
		((WebSocket) this.webSocketServer.connections().toArray()[0]).send(message);
	}
	
	private void endWait() {
		String message = "endWait";
		((WebSocket) this.webSocketServer.connections().toArray()[0]).send(message);
	}
	
	private void allDrawingsReady() {
		String message = "allDrawingsReady#".concat(Integer.toString(this.game.wordChooseTime));
		((WebSocket) this.webSocketServer.connections().toArray()[0]).send(message);
	}
	
	private void voteWord() {
		String message = "voteWord#".concat(Integer.toString(this.game.voteTime));
		((WebSocket) this.webSocketServer.connections().toArray()[0]).send(message);
	}
	
	private void showPoints() { // Host wil animate points and send message to server that players can be woken up from waiting.
		String message = "showPoints#";
		message = message.concat(this.game.getCorrectWord()).concat("#");
		PlayerData currentPlayerData = this.game.getCurrentPlayerData();
		message = message.concat(currentPlayerData.name).concat("#");
		message = message.concat(currentPlayerData.colors.color1).concat("#");
		message = message.concat(currentPlayerData.colors.color2).concat("#");
		message = message.concat(Integer.toString(currentPlayerData.index)).concat("#");
		ArrayList<String> wrongWords = this.game.getWrongWords();
		message = message.concat(Integer.toString(wrongWords.size())).concat("#");
		for (String wrongWord : wrongWords) {
			message = message.concat(wrongWord).concat("#");
			PlayerData thisPlayer = this.game.getPlayerWrongWord(wrongWord);
			message = message.concat(thisPlayer.name).concat("#");
			message = message.concat(thisPlayer.colors.color1).concat("#");
			message = message.concat(thisPlayer.colors.color2).concat("#");
			message = message.concat(Integer.toString(thisPlayer.index)).concat("#");
			ArrayList<PlayerData> wrongPlayers = this.game.getWrongPlayers(wrongWord);
			message = message.concat(Integer.toString(wrongPlayers.size())).concat("#");
			for (PlayerData wrongPlayer : wrongPlayers) {
				message = message.concat(wrongPlayer.name).concat("#");
				message = message.concat(wrongPlayer.colors.color1).concat("#");
				message = message.concat(wrongPlayer.colors.color2).concat("#");
				message = message.concat(Integer.toString(wrongPlayer.index)).concat("#");
			}
		}
		message = message.concat(Integer.toString(this.game.getNumberPlayers())).concat("#");
		for (int i = 0; i < this.game.getNumberPlayers(); i++) {
			message = message.concat(Integer.toString(this.game.getOldPlayerScore(i))).concat("#");
		}
		((WebSocket) this.webSocketServer.connections().toArray()[0]).send(message);
	}
}
