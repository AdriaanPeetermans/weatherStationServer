package testers;

import java.util.HashSet;

import timingHandlers.BASISTimeUpdater;
import timingHandlers.BASISTransferFilesUpdater;
import timingHandlers.TimingHandler;
import timingHandlers.Updater;
import timingHandlers.helpers.DayTime;

public class TimingHandler_tb {

	public static void main(String[] args) {
		HashSet<Updater> updaters = new HashSet<Updater>(1);
		updaters.add(new BASISTimeUpdater("192.168.1.61", 9876, new DayTime(23, 57)));
		//updaters.add(new BASISTransferFilesUpdater("192.168.1.61", 9876, 1));
		TimingHandler handler = new TimingHandler(updaters);
		handler.start();
	}

}
