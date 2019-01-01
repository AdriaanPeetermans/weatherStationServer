package timingHandlers;

import java.util.HashSet;

public class TimingHandler {

	public TimingHandler(HashSet<Updater> updaters) {
		this.updaters = updaters;
	}
	
	private final HashSet<Updater> updaters;
	
	public void start() {
		while (true) {
			for (Updater updater : this.updaters) {
				if (updater.canRun()) {
					updater.run();
				}
			}
		}
	}
}
