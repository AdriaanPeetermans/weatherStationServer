package dataBase.helpers;

public class FileFolder implements Comparable<FileFolder> {

	public FileFolder(int type, String name, String size, String onDisk) {
		this.type = type;
		this.name = name;
		this.size = size;
		this.onDisk = onDisk;
	}
	
	/**
	 * Indicating if it is a file (1) or a folder (0).
	 */
	public final int type;
	
	public final String name;
	
	public final String size;
	
	public final String onDisk;
	
	public int compareTo(FileFolder o) {
		return this.name.compareTo(o.name);
	}
}
