package testers;

import mailServer.MailServer;

public class MailServer_tb {

	public static void main(String[] args) {
		MailServer server = new MailServer(30, "weatherstation.basis@gmail.com", "w3Ath3r5tAt10n", "weatherstation.basis@gmail.com", "192.168.1.61", 9876, 10);
		server.start();
	}

}
