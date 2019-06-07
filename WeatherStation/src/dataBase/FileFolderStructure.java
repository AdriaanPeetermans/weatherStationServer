package dataBase;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import dataBase.helpers.DateObject;
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
				HardDriveParser parser = new HardDriveParser();
				String filePath = listOfFiles[i].getPath();
				String[] pathParts = filePath.split("/");
				String onDisk;
				if ((!pathParts[2].equals("BASIS")) && (!pathParts[2].equals("SENSOR1")) && (!pathParts[2].equals("SENSOR2"))) {
					onDisk = "false";
				}
				else {
					int day = Integer.parseInt(listOfFiles[i].getName().split("\\.")[0]);
					int month = Integer.parseInt(pathParts[pathParts.length-2]);
					int year = Integer.parseInt(pathParts[pathParts.length-3])-2000;
					String type = pathParts[pathParts.length-4];
					onDisk = Boolean.toString(parser.onDisk(new DateObject(day, month, year, type)));
				}
				result.add(new FileFolder(1, listOfFiles[i].getName(), Double.toString(((double) listOfFiles[i].length())/1000), onDisk));
			}
			else if (listOfFiles[i].isDirectory()) {
				result.add(new FileFolder(0, listOfFiles[i].getName(), "-", "-"));
			}
		}
		result.sort(null);
		return result;
	}
}
