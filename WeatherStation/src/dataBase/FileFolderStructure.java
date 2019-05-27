package dataBase;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import dataBase.helpers.FileFolder;

public class FileFolderStructure {
	
	public FileFolderStructure() {
		
	}
	
	public ArrayList<FileFolder> giveContents(String pathString) {
		File folder = new File(pathString);
		Path path = folder.toPath();
		if (Files.notExists(path)) {
			return null;
		}
		ArrayList<FileFolder> result = new ArrayList<FileFolder>(31);
		File[] listOfFiles = folder.listFiles();
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].getName().equals(".DS_Store")) {
				continue;
			}
			if ((pathString.equals("src/dataBase")) && (listOfFiles[i].isFile())) {
				continue;
			}
			if ((pathString.equals("src/dataBase")) && (listOfFiles[i].getName().equals("helpers"))) {
				continue;
			}
			if ((pathString.equals("src/dataBase")) && (listOfFiles[i].getName().equals("ZipFiles"))) {
				continue;
			}
			if (listOfFiles[i].isFile()) {
				result.add(new FileFolder(1, listOfFiles[i].getName(), Double.toString(((double) listOfFiles[i].length())/1000)));
			}
			else if (listOfFiles[i].isDirectory()) {
				result.add(new FileFolder(0, listOfFiles[i].getName(), "-"));
			}
		}
		result.sort(null);
		return result;
	}
}
