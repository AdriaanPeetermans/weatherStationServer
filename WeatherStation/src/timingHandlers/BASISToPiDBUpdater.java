package timingHandlers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import basis.BasisHandler;
import dataBase.DataBaseManager;
import dataBase.Parser;
import exceptions.BasisException;
import timingHandlers.helpers.DayTime;

public class BASISToPiDBUpdater extends Updater {

	public BASISToPiDBUpdater(String basisIP, int basisPort, int minutesInterval) {
		super(basisIP, basisPort, minutesInterval);
		this.bh = new BasisHandler(this.ip, this.port, 10, 5, 5);
	}
	
	public BASISToPiDBUpdater(String basisIP, int basisPort, DayTime when) {
		super(basisIP, basisPort, when);
		this.bh = new BasisHandler(this.ip, this.port, 10, 5, 5);
	}
	
	private final BasisHandler bh;
	
	public void run() {
		super.run();
		try {
			ArrayList<String> data = this.bh.getBasisToPiData();
			DataBaseManager dbManager = new DataBaseManager("BASIS");
			for (int i = data.size()-1; i >= 0; i--) {
				int lastDays = BASISToPiDBUpdater.getLastDays(data.get(i).split(" ")[2]);
				Parser parser = dbManager.addFolder(lastDays);
				parser.appendFile(data.get(i).concat("\r\n"));
			}
		}
		catch (BasisException e) {
			e.printStackTrace();
		}
	}
	
	private static int getLastDays(String date) {
		String[] parts = date.split("/");
		int day = Integer.parseInt(parts[0]);
		int month = Integer.parseInt(parts[1]);
		int year = 2000+Integer.parseInt(parts[2]);
		Calendar now = Calendar.getInstance();
		Calendar cal = (Calendar) now.clone();
		cal.set(Calendar.DATE, day);
		cal.set(Calendar.MONTH, month-1);
		cal.set(Calendar.YEAR, year);
		return (int) TimeUnit.MILLISECONDS.toDays(Math.abs(now.getTimeInMillis() - cal.getTimeInMillis()));
	}
}
