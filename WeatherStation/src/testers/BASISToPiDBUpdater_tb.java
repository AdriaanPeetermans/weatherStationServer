package testers;

import timingHandlers.BASISToPiDBUpdater;

public class BASISToPiDBUpdater_tb {

	public static void main(String[] args) {
		BASISToPiDBUpdater BTPUpdater = new BASISToPiDBUpdater("192.168.1.61", 9876, 2);
		BTPUpdater.run();
	}

}
