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
	
	private ArrayList<String> words = new ArrayList<String>(8);
	
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
	
	private ArrayList<ArrayList<Segment>> drawings = new ArrayList<ArrayList<Segment>>(8);
	
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
		ArrayList<String> words = parser.getWords(this.players.size());
		for (int i = 0; i < this.players.size(); i++) {
			this.players.get(i).notify("startGame", words.get(i));
		}
		this.host.notify("startGameHost", null);
		return this.players.size();
	}
	
	public void addWord(String word, int playerIndex) {
		this.words.add(playerIndex, word);
	}
	
	public ArrayList<String> getWords() {
		ArrayList<String> result = new ArrayList<String>(this.players.size());
		for (int i = 0; i < this.players.size(); i++) {
			result.add(this.words.get(i));
		}
		Collections.shuffle(result);
		return result;
	}
	
	public int getNumberPlayers() {
		return this.players.size();
	}
}
