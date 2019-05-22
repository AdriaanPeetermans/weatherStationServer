package testers;

import java.util.ArrayList;

import dataBase.FileFolderStructure;
import dataBase.helpers.FileFolder;

public class FileFolderStructure_tb {

	public static void main(String[] args) {
		FileFolderStructure ffs = new FileFolderStructure();
		ArrayList<FileFolder> files = ffs.giveContents("src/dataBase/SENSOR1/2019/03");
		for (FileFolder i : files) {
			System.out.println(Integer.toString(i.type).concat(" ").concat(i.name));
		}
	}

}
