package testers;

import timingHandlers.HardDriveUpdater;
import timingHandlers.helpers.DayTime;

public class HardDriveUpdater_tb {

	public static void main(String[] args) {
		HardDriveUpdater hdu = new HardDriveUpdater("afp://WDMyCloud._afpovertcp._tcp.local/Adriaan/weatherStation/data/", new DayTime(0, 1));
		hdu.run();
	}

}
