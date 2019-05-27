package testers;

import timingHandlers.BASISToPiDBUpdater;

public class BASISToPiDBUpdater_tb {

	public static void main(String[] args) {
		BASISToPiDBUpdater BTPUpdater = new BASISToPiDBUpdater("127.0.0.1", 9875, 2);
		BTPUpdater.run();
	}

}
