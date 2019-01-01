package testers;

import timingHandlers.BASISForecastUpdater;

public class BASISForecastUpdater_tb {

	public static void main(String[] args) {
		BASISForecastUpdater updater = new BASISForecastUpdater("192.168.1.61", 9876, 60);
		updater.run();
	}

}
