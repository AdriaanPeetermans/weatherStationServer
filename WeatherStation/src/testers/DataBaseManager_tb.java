package testers;

import dataBase.DataBaseManager;

public class DataBaseManager_tb {

	public static void main(String[] args) {
		DataBaseManager manager = new DataBaseManager("BASIS");
		manager.addFolder(20);
	}

}
