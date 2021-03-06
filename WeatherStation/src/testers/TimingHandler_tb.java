package testers;

import java.util.HashSet;

import timingHandlers.BASISForecastUpdater;
import timingHandlers.BASISTimeUpdater;
import timingHandlers.BASISToPiDBUpdater;
import timingHandlers.HardDriveUpdater;
import timingHandlers.TimingHandler;
import timingHandlers.Updater;
import timingHandlers.helpers.DayTime;

public class TimingHandler_tb {

	public static void main(String[] args) {
		HashSet<Updater> updaters = new HashSet<Updater>(3);
		updaters.add(new BASISTimeUpdater("192.168.1.61", 9876, 15));
		updaters.add(new BASISToPiDBUpdater("192.168.1.61", 9876, 30));
		updaters.add(new BASISForecastUpdater("192.168.1.61", 9876, 60));
		//updaters.add(new HardDriveUpdater("", new DayTime(0, 1)));
		//updaters.add(new BASISTransferFilesUpdater("192.168.1.61", 9876, 1));
		TimingHandler handler = new TimingHandler(updaters);
		handler.start();
	}

}
