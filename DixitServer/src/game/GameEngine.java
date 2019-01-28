package game;

import java.util.ArrayList;
import java.util.Collections;

import game.words.WordsParser;
import helpers.playerData.PlayerData;
import helpers.playerData.Segment;
import servers.GameServer;

public class GameEngine {
	
	public GameEngine(String code, GameServer host) {
		this.code = code;
		this.host = host;
		for (int i = 0; i < 8; i++) {
			this.playerDatas.add(i, null);
			this.drawings.add(i, null);
		}
	}
	
	public final String code;
	
	public final GameServer host;
	
	private ArrayList<GameServer> players = new ArrayList<GameServer>(8);
	
	private int playerIndex = 0;
	
	public ArrayList<PlayerData> playerDatas = new ArrayList<PlayerData>(8);
	
	public final int wordTime = 150;
	
	public final int wordChooseTime = 125;
	
	public final int voteTime = 100;
	
	private String[] words = new String[8];
	
	public int wordNumber = 0;
	
	public int[] points = new int[8];
	
	/**
	 * GameState:
	 * 		0:	add players phase.
	 * 		1:	game has started, words are distributed.
	 * 		2:	players describe drawings.
	 * 		3:	players vote for word.
	 */
	public int state = 0;
	
	private void initPlayersReady() {
		for (int i = 0; i < this.players.size(); i++) {
			this.playersReady[i] = false;
		}
	}
	
	private boolean[] playersReady = {false, false, false, false, false, false, false, false};
	
	public void addDrawing(ArrayList<Segment> drawing, int playerIndex) {
		this.drawings.add(playerIndex, drawing);
		this.host.notify("drawingReady", playerIndex);
		this.playersReady[playerIndex] = true;
		this.checkAllPlayersReadyDrawing();
	}
	
	private void checkAllPlayersReadyDrawing() {
		if (!this.goFurther()) {
			return;
		}
		this.initPlayersReady();
		this.playersReady[this.drawingPerm.get(this.wordNumber)] = true;
		for (int i = 0; i < this.players.size(); i++) {
			this.players.get(i).notify("endWait", null);
		}
		this.state = 2;
		this.host.notify("allDrawings", null);
	}
	
	private boolean goFurther() {
		for (int i = 0; i < this.players.size(); i++) {
			if (!this.playersReady[i]) {
				return false;
			}
		}
		return true;
	}
	
	public ArrayList<Segment> getDrawing(int playerIndex) {
		return this.drawings.get(playerIndex);
	}
	
	public ArrayList<Segment> getRandomDrawing(int drawingNumber) {
		return this.getDrawing(this.drawingPerm.get(drawingNumber));
	}
	
	private ArrayList<Integer> drawingPerm;
	
	private ArrayList<ArrayList<Segment>> drawings = new ArrayList<ArrayList<Segment>>(8);
	
	private ArrayList<String> realWords = new ArrayList<String>(8);
	
	public synchronized int addPlayer(GameServer player) {
		if (!this.players.contains(player)) {
			this.players.add(this.playerIndex, player);
		}
		this.playerIndex ++;
		this.host.notify("newPlayer", this.playerIndex-1);
		return this.playerIndex - 1;
	}
	
	public synchronized void removePlayer(GameServer player) {
		if (this.players.contains(player)) {
			this.players.remove(player);
		}
	}
	
	public synchronized void addPlayerDrawing(PlayerData playerData, int index) {
		this.playerDatas.remove(index);
		this.playerDatas.add(index, playerData);
		this.host.notify("addPlayer", playerData);
	}
	
	public synchronized int startGame() {
		this.state = 1;
		WordsParser parser = new WordsParser("NL");
		this.realWords = parser.getWords(this.players.size());
		this.drawingPerm = new ArrayList<Integer>(this.players.size());
		for (int i = 0; i < this.players.size(); i++) {
			this.players.get(i).notify("startGame", this.realWords.get(i));
			this.drawingPerm.add(i);
		}
		this.host.notify("startGameHost", null);
		Collections.shuffle(this.drawingPerm);
		return this.players.size();
	}
	
	public void addWord(String word, int playerIndex) {
		word = this.adjustWord(word);
		this.words[playerIndex] = word;
		this.playersReady[playerIndex] = true;
		this.checkAllPlayersReadyWord();
	}
	
	private void checkAllPlayersReadyWord() {
		if (!this.goFurther()) {
			return;
		}
		this.initPlayersReady();
		this.playersReady[this.drawingPerm.get(this.wordNumber)] = true;
		for (int i = 0; i < this.players.size(); i++) {
			this.players.get(i).notify("endWait", null);
		}
		this.state = 3;
		this.host.notify("voteWord", null);
	}
	
	public ArrayList<String> getWords(int playerNumber) {
		ArrayList<String> result = new ArrayList<String>(this.players.size());
		for (int i = 0; i < this.players.size(); i++) {
			if (i != playerNumber) {
				result.add(this.words[i]);
			}
		}
		result.add(this.realWords.get(this.drawingPerm.get(this.wordNumber)));
		Collections.shuffle(result);
		return result;
	}
	
	public int getNumberPlayers() {
		return this.players.size();
	}
	
	public int getCurrentPlayer() {
		return this.drawingPerm.get(this.wordNumber);
	}
	
	private String adjustWord(String word) {
		word = word.toLowerCase();
		word = word.substring(0,1).toUpperCase().concat(word.substring(1));
		return word;
	}
	
	public void voted(int playerNumber, String word) {
		if (word.equals(this.realWords.get(this.drawingPerm.get(this.wordNumber)))) { //Correct vote
			this.points[playerNumber] ++;
			this.points[this.drawingPerm.get(this.wordNumber)] += 2;
		}
		else {
			for (int i = 0; i < this.players.size(); i++) {
				if ((this.words[i].equals(word)) && (i != playerNumber)) { //Mislead
					this.points[i] ++;
				}
			}
		}
		this.checkAllPlayersReadyVoting();
	}
	
	private void checkAllPlayersReadyVoting() {
		if (!this.goFurther()) {
			return;
		}
		this.initPlayersReady();
//		for (int i = 0; i < this.players.size(); i++) {
//			this.players.get(i).notify("endWait", null);
//		}
		System.out.println("stemmen klaar");
		this.state = 1;
		this.host.notify("showPoints", null);
	}
}
