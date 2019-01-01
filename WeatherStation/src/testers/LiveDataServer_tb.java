package testers;

import siteServer.LiveDataServer;

public class LiveDataServer_tb {

	public static void main(String[] args) {
		LiveDataServer server = new LiveDataServer(9001, "192.168.1.61", 9876, 10, 5, 5);
		server.start();
	}

}
