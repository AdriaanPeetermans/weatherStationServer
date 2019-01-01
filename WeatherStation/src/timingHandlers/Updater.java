package timingHandlers;

public abstract class Updater extends Thread {
	
	public Updater(String ip, int port, int minutesInterval) {
		this.ip = ip;
		this.port = port;
		this.minutesInterval = minutesInterval;
		this.deadline = System.currentTimeMillis();
	}
	
	public final String ip;
	
	public final int port;
	
	public final int minutesInterval;
	
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
