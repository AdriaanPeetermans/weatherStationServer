package siteServer;

import java.util.ArrayList;

import dataBase.FileFolderStructure;
import dataBase.Parser;
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
	 * 
	 * Outgoing message:
	 * 		type 0: 0#numberOfFileFolders#type0#sizeString0#name0# ... typen#sizeStringn#namen#
	 * 		type 1: 1#numberOfLines#sizeInBytes#line0#line1# ... #linen#
	 * 
	 * If error: ERROR
	 */
	@Override
	public void handleMessage(SocketMessage t) {
		System.out.println(t.message);
		String[] parts = t.message.split("#");
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
					answer = answer.concat(Integer.toString(i.type)).concat("#").concat(i.size).concat("#").concat(i.name).concat("#");
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
}
