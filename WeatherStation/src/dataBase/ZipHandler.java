package dataBase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipHandler {
	
	public ZipHandler() {
		File f = new File("src/dataBase/ZipFiles/data.zip");
		try {
			this.out = new ZipOutputStream(new FileOutputStream(f));
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		this.folder = "";
	}
	
	private ZipOutputStream out;
	
	private String folder;
	
	public void enterFolder(String folderName) {
		this.folder = this.folder.concat(folderName).concat("/");
	}
	
	public void exitFolder() {
		String[] parts = this.folder.split("/");
		this.folder = "";
		for (int i = 0; i < parts.length-1; i++) {
			this.folder = this.folder.concat(parts[i]).concat("/");
		}
	}
	
	public void addFileToZip(String path) {
		try {
			String[] parts = path.split("/");
			ZipEntry e = new ZipEntry(this.folder.concat(parts[parts.length-1]));
			this.out.putNextEntry(e);
			StringBuilder sb = new StringBuilder();
			Parser parser = new Parser(parts[1], Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4].substring(0, 2)), true);
			ArrayList<String> lines = parser.readLines();
			for (String line : lines) {
				sb.append(line.concat("\r\n"));
			}
			byte[] data = sb.toString().getBytes();
			this.out.write(data, 0, data.length);
			this.out.closeEntry();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void addFolderToZip(String path) {
		String[] pathParts = path.split("/");
		this.enterFolder(pathParts[pathParts.length-1]);
		String newPath = path.substring(8);
		newPath = "src/dataBase".concat(newPath);
		File folder = new File(newPath);
		Path folderPath = folder.toPath();
		if (Files.notExists(folderPath)) {
			return;
		}
		File[] listOfFiles = folder.listFiles();
		for (File file : listOfFiles) {
			if (file.isDirectory()) {
				this.addFolderToZip(path.concat("/").concat(file.getName()));
			}
			else {
				if (file.getName().equals(".DS_Store")) {
					continue;
				}
				this.addFileToZip(path.concat("/").concat(file.getName()));
			}
		}
		this.exitFolder();
	}
	
	public byte[] getZip() {
		try {
			out.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		File dataFile = new File("src/dataBase/ZipFiles/data.zip");
		Path dataPath = dataFile.toPath();
		try {
			return java.nio.file.Files.readAllBytes(dataPath);
		} catch (IOException e) {
			e.printStackTrace();
		};
		byte[] otherResult = new byte[0];
		return otherResult;
	}
}
