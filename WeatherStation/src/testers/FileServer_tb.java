package testers;

import siteServer.FilesServer;

public class FileServer_tb {

	public static void main(String[] args) {
		FilesServer server = new FilesServer(9002);
		server.start();
	}

}
