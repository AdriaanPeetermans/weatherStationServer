package timingHandlers;

import basis.BasisHandler;
import exceptions.BasisException;

public class BASISTransferFilesUpdater extends Updater {
	
	public BASISTransferFilesUpdater(String ip, int port, int minutesInterval) {
		super(ip, port, minutesInterval);
		this.basisHandler = new BasisHandler(this.ip, this.port, 10, 5);
	}
	
	private final BasisHandler basisHandler;
	
	public void run() {
		super.run();
		try {
			int dayIndex = this.basisHandler.getDayIndex();
			int prevIndex = dayIndex-1;
			if (prevIndex < 0) {
				prevIndex = prevIndex + this.basisHandler.maxDays;
			}
			this.basisHandler.transferFiles(prevIndex);
		}
		catch (BasisException e) {
			e.printStackTrace();
		}
	}
}
