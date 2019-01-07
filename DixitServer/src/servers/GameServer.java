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
	 * 		returns:		two colors in hexadecimal format, separated by "#"
	 * 
	 * 		name:			addPlayer		(receive player name and drawing)
	 * 		parameters:		name
	 * 						segment1%segment2%segment3% ..., segment: color&size&length&x0&y0&x1&y1& ...
	 * 		gameState		0
	 * 		returns:		1 if player is VIP, 0 otherwise
	 */
	@Override
	public void handleMessage(SocketMessage t) {
		String[] parts = t.message.split("#");
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
		return colors.color1.concat("#").concat(colors.color2);
	}
	
	private int addPlayer(String name, String drawing) {
		String[] segments = drawing.split("%");
		ArrayList<Segment> segs = new ArrayList<Segment>(segments.length);
		for (String seg : segments) {
			String[] segParts = seg.split("&");
			String color = segParts[0];
			float size = Float.parseFloat(segParts[1]);
			int segLength = Integer.parseInt(segParts[2]);
			ArrayList<Integer> segx = new ArrayList<Integer>(segLength);
			ArrayList<Integer> segy = new ArrayList<Integer>(segLength);
			for (int i = 0; i < segLength; i++) {
				segx.add(Integer.parseInt(segParts[3+2*i]));
				segy.add(Integer.parseInt(segParts[4+2*i]));
			}
			Segment segment = new Segment(segx, segy, color, size);
			segs.add(segment);
		}
		PlayerData playerData = new PlayerData(name, segs, this.mainServer.playerColors.get(this.playerIndex), this.playerIndex);
		this.game.addPlayerDrawing(playerData, this.playerIndex);
		if (this.playerIndex == 0) {
			return 1;
		}
		else {
			return 0;
		}
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
	 */
	public void notify(String reason, Object params) {
		switch (reason) {
			case "newPlayer":
				int playerIndex = (int) params;
				this.newPlayer(playerIndex);
				break;
			case "addPlayer":
				PlayerData playerData = (PlayerData) params;
				this.addPlayer(playerData);
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
}
