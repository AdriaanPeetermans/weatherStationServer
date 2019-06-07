package siteServer;

import java.io.File;
import java.util.ArrayList;

import dataBase.FileFolderStructure;
import dataBase.Parser;
import dataBase.ZipHandler;
import dataBase.helpers.FileFolder;
import siteServer.helpers.SocketMessage;

public class FilesServer extends Server {

	public FilesServer(int port) {
		super(port);
		this.ffs = new FileFolderStructure();
	}
	
	private final FileFolderStructure ffs;
	
	/**
	 * Incoming message: type#database/path/to/folder. Always starting with database.
	 * type: 0: folder, 1: file.
	 * type: 2: download files/folders, message: 2#path/to/parent/folder#numberFileFolders#fileFolderName0# ... #fileFolderNamen#
	 * 
	 * Outgoing message:
	 * 		type 0: 0#numberOfFileFolders#type0#sizeString0#onDisk0#name0# ... typen#sizeStringn#onDiskn#namen#
	 * 		type 1: 1#numberOfLines#sizeInBytes#line0#line1# ... #linen#
	 * 		type 2: 2#length#zipFile
	 * 
	 * If error: ERROR
	 */
	@Override
	public void handleMessage(SocketMessage t) {
		System.out.println(t.message);
		String[] parts = t.message.split("#");
		if (Integer.parseInt(parts[0]) == 2) {
			this.downloadFileFolders(t, parts);
			return;
		}
		if (parts.length != 2) {
			t.sock.send("ERROR");
			return;
		}
		int type = Integer.parseInt(parts[0]);
		String mes = parts[1];
		System.out.println(mes);
		if (mes.length() < 8) {
			t.sock.send("ERROR");
			return;
		}
		if (!mes.substring(0,8).equals("database")) {
			t.sock.send("ERROR");
			return;
		}
		mes = mes.substring(8);
		String path = "src/dataBase".concat(mes);
		String answer;
		switch (type) {
			case 0:
				ArrayList<FileFolder> files = this.ffs.giveContents(path);
				if (files == null) {
					t.sock.send("ERROR");
					return;
				}
				answer = "0#".concat(Integer.toString(files.size())).concat("#");
				for (FileFolder i : files) {
					answer = answer.concat(Integer.toString(i.type)).concat("#").concat(i.size).concat("#").concat(i.onDisk).concat("#").concat(i.name).concat("#");
				}
				System.out.println(answer);
				t.sock.send(answer);
				break;
			case 1:
				String[] pathParts = mes.split("/");
				if (pathParts.length != 5) {
					t.sock.send("ERROR");
					return;
				}
				String dataType = pathParts[1];
				int year = Integer.parseInt(pathParts[2]);
				int month = Integer.parseInt(pathParts[3]);
				int day = Integer.parseInt(pathParts[4].substring(0, 2));
				Parser parser = new Parser(dataType, year, month, day, true);
				ArrayList<String> lines = parser.readLines();
				answer = "1#".concat(Integer.toString(lines.size())).concat("#").concat(parser.getSize()).concat("#");
				for (String line : lines) {
					answer = answer.concat(line).concat("#");
				}
				System.out.println(answer);
				t.sock.send(answer);
				break;
		}
	}
	
	/**
	 * type: 2: download files/folders, message: 2#path/to/parent/folder#numberFileFolders#fileFolderName0# ... #fileFolderNamen#
	 */
	public void downloadFileFolders(SocketMessage t, String[] parts) {
		String parentPath = parts[1];
		int number = Integer.parseInt(parts[2]);
		ZipHandler zipHandler = new ZipHandler();
		for (int i = 0; i < number; i++) {
			File file = new File("src/dataBase".concat(parentPath.substring(8)).concat("/").concat(parts[i+3]));
			if (file.isDirectory()) {
				zipHandler.addFolderToZip(parentPath.concat("/").concat(parts[i+3]));
			}
			else {
				zipHandler.addFileToZip(parentPath.concat("/").concat(parts[i+3]));
			}
		}
		byte[] dataFile = zipHandler.getZip();
		byte[] dataLength = "2#".concat(Integer.toString(dataFile.length)).concat("#").getBytes();
		byte[] answer = new byte[dataLength.length+dataFile.length];
		for (int i = 0; i < dataLength.length; i++) {
			answer[i] = dataLength[i];
		}
		for (int i = 0; i < dataFile.length; i++) {
			answer[i+dataLength.length] = dataFile[i];
		}
		t.sock.send(answer);
	}
}
