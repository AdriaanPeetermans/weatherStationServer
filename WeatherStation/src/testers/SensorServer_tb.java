package testers;

import java.io.IOException;

import basis.SensorServer;
import exceptions.BasisException;

public class SensorServer_tb {

	public static void main(String[] args) throws IOException, BasisException {
		SensorServer server = new SensorServer("127.0.0.1", 6756, 6757);
		server.run();
	}

}
