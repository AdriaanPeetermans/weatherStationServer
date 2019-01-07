package servers;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import game.GameEngine;
import helpers.Server;
import helpers.SocketMessage;
import helpers.playerData.PlayerColors;

public class DistributerServer extends Server {
	
	public DistributerServer(int startPort) {
		super(startPort);
	}
	
	private HashSet<Integer> takenPorts = new HashSet<Integer>(42);
	
	public final List<PlayerColors> playerColors = Arrays.asList(new PlayerColors("E3824D", "4DAEE3"), new PlayerColors("4DE3A4", "E34D8C"), new PlayerColors("80A0D9", "D9B980"), new PlayerColors("80D9D1", "D98088"), new PlayerColors("E898B3", "98E8CD"), new PlayerColors("EDD59F", "9FB7ED"), new PlayerColors("CBED9F", "C19FED"), new PlayerColors("ED9FCD", "9FEDBF"));
	
	/**
	 * Message:
	 * 		functionName + # + param0 + # + param1 + # + ...
	 * 
	 * Functions:
	 * 		name:			getServer
	 * 		parameters:		none
	 * 		returns:		port integer
	 */
	@Override
	public void handleMessage(SocketMessage t) {
		String[] parts = t.message.split("#");
		switch (parts[0]) {
			case "getServer":
				String answer = Integer.toString(this.getServer());
				t.sock.send(answer);
				break;
		}
	}
	
	private int getServer() {
		int i = this.port + 1;
		while (this.takenPorts.contains(i)) {
			i ++;
		}
		this.addPort(i);
		GameServer gameServer = new GameServer(i, this);
		gameServer.start();
		return i;
	}
	
	public void handleClose(SocketMessage t) {
		return;
	}
	
	public synchronized void removePort(int port) {
		if (this.takenPorts.contains(port)) {
			this.takenPorts.remove(port);
		}
	}
	
	private synchronized void addPort(int port) {
		if (!this.takenPorts.contains(port)) {
			this.takenPorts.add(port);
		}
	}
	
	public synchronized GameEngine addGame(GameServer host) {
		GameEngine engine = new GameEngine(this.generateCode(), host);
		this.games.add(engine);
		return engine;
	}
	
	public synchronized void removeGame(GameEngine engine) {
		this.games.remove(engine);
	}
	
	private HashSet<GameEngine> games = new HashSet<GameEngine>(42);
	
	public synchronized boolean existCode(String code) {
		for (GameEngine engine : this.games) {
			if (engine.code.equals(code)) {
				return true;
			}
		}
		return false;
	}
	
	public synchronized GameEngine getEngine(String code) {
		for (GameEngine engine : this.games) {
			if (engine.code.equals(code)) {
				return engine;
			}
		}
		return null;
	}
	
	private String generateCode() {
		return "1A2B3C";
	}
}
