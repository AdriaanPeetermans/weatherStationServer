package timingHandlers.helpers;

public class DayTime {
	
	public DayTime(int hour, int minute) {
		if ((hour >= 0) && (hour < 24)) {
			this.hour = hour;
		}
		else {
			this.hour = 0;
		}
		if ((minute >= 0) && (minute < 60)) {
			this.minute = minute;
		}
		else {
			this.minute = 0;
		}
	}
	
	public final int hour;
	
	public final int minute;
}
