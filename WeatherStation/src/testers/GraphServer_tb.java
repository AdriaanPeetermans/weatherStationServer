package testers;

import siteServer.graphServer;

public class GraphServer_tb {

	public static void main(String[] args) {
		graphServer server = new graphServer(9876);
		server.start();
	}

}
