package timingHandlers;

import java.util.Calendar;

import timingHandlers.helpers.DayTime;

public abstract class Updater extends Thread {
	
	public Updater(String ip, int port, int minutesInterval) {
		this.ip = ip;
		this.port = port;
		this.minutesInterval = minutesInterval;
		this.deadline = System.currentTimeMillis();
		this.when = null;
	}
	
	public Updater(String ip, int port, DayTime when) {
		this.ip = ip;
		this.port = port;
		this.when = when;
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, when.hour);
		cal.set(Calendar.MINUTE, when.minute);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		this.deadline = cal.getTimeInMillis();
		if (this.deadline < System.currentTimeMillis()) {
			this.deadline = this.deadline + 1000*60*60*24;
		}
		this.minutesInterval = 60*24;
	}
	
	public final String ip;
	
	public final int port;
	
	public final int minutesInterval;
	
	public final DayTime when;
	
	public long deadline;
	
	public boolean canRun() {
		return (System.currentTimeMillis() > this.deadline);
	}
	
	public void run() {
		this.deadline = this.deadline + this.minutesInterval*60*1000;
		System.out.println("Running: ".concat(this.toString()));
		System.out.println(this.deadline);
	}
}