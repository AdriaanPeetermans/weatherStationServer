package timingHandlers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Calendar;
import java.util.HashSet;

import dataBase.HardDriveParser;
import dataBase.helpers.DateObject;
import timingHandlers.helpers.DayTime;

public class HardDriveUpdater extends Updater {
	
	public HardDriveUpdater(String dbPath, int minutesInterval) {
		super(null, 0, minutesInterval);
		this.dbPath = dbPath;
		this.parser = new HardDriveParser();
	}
	
	public HardDriveUpdater(String dbPath, DayTime when) {
		super(null, 0, when);
		this.dbPath = dbPath;
		this.parser = new HardDriveParser();
	}
	
	public final String originPath = "src/dataBase/";
	
	private final String dbPath;
	
	private final HardDriveParser parser;
	
	public void run() {
		super.run();
		this.collectFiles();
		this.copyFiles();
	}

	private void copyFiles() {
		HashSet<DateObject> dates = this.parser.getDates();
		for (DateObject date : dates) {
			String relativeFilePath = date.type.concat("/").concat(String.format("%04d", date.year+2000)).concat("/").concat(String.format("%02d", date.month)).concat("/").concat(String.format("%02d", date.day)).concat(".txt");	
			String originFilePath = this.originPath.concat(relativeFilePath);
			String destFilePath = this.dbPath.concat(relativeFilePath);
			File originFile = new File(originFilePath);
			if (!originFile.exists()) {
				continue;
			}
			File destFile = new File(destFilePath);
			destFile.mkdirs();
			destFile.delete();
			try {
				Files.copy(originFile.toPath(), destFile.toPath());
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			}
			this.parser.removeDate(date);
		}
	}
	
	private void collectFiles() {
		Calendar yesterday = Calendar.getInstance();
		yesterday.add(Calendar.DAY_OF_YEAR, -1);
		this.parser.appendDate(new DateObject(yesterday.get(Calendar.DAY_OF_MONTH), yesterday.get(Calendar.MONTH)+1, yesterday.get(Calendar.YEAR)-2000, "BASIS"));
		this.parser.appendDate(new DateObject(yesterday.get(Calendar.DAY_OF_MONTH), yesterday.get(Calendar.MONTH)+1, yesterday.get(Calendar.YEAR)-2000, "SENSOR1"));
		this.parser.appendDate(new DateObject(yesterday.get(Calendar.DAY_OF_MONTH), yesterday.get(Calendar.MONTH)+1, yesterday.get(Calendar.YEAR)-2000, "SENSOR2"));
	}
}
