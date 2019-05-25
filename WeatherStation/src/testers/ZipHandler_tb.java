package testers;

import dataBase.ZipHandler;

public class ZipHandler_tb {

	public static void main(String[] args) {
		ZipHandler handler = new ZipHandler();
		//handler.addFileToZip("database/SENSOR1/2019/05/01.txt");
		handler.addFolderToZip("database/SENSOR1/2019");
		handler.getZip();
	}

}
