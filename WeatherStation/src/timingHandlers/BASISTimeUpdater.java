package timingHandlers;

import basis.BasisHandler;
import exceptions.BasisException;

public class BASISTimeUpdater extends Updater {

	public BASISTimeUpdater(String ip, int port, int minutesInterval) {
		super(ip, port, minutesInterval);
		this.basisHandler = new BasisHandler(this.ip, this.port, 10, 5);
	}
	
	private final BasisHandler basisHandler;
	
	public void run() {
		super.run();
		try {
			this.basisHandler.setTime();
		}
		catch (BasisException e) {
			e.printStackTrace();
		}
	}

}
