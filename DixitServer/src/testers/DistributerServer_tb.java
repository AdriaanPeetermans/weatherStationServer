package testers;

import servers.DistributerServer;

public class DistributerServer_tb {

	public static void main(String[] args) {
		DistributerServer server = new DistributerServer(9010);
		server.start();
//		GameServer host = new GameServer(9100, server);
//		server.addGame(host);
	}

}
