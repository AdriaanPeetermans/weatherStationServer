package game;

import java.util.ArrayList;

import helpers.playerData.PlayerData;
import servers.GameServer;

public class GameEngine {
	
	public GameEngine(String code, GameServer host) {
		this.code = code;
		this.host = host;
		for (int i = 0; i < 8; i++) {
			this.playerDatas.add(i, null);
		}
	}
	
	public final String code;
	
	public final GameServer host;
	
	private ArrayList<GameServer> players = new ArrayList<GameServer>(8);
	
	private int playerIndex = 0;
	
	public ArrayList<PlayerData> playerDatas = new ArrayList<PlayerData>(8);
	
	/**
	 * GameState:
	 * 		0:	add players phase.
	 * 		1:	
	 */
	public int state = 0;
	
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
}
