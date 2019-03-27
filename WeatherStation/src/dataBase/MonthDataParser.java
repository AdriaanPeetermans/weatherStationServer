package dataBase;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;

public abstract class MonthDataParser {
	
	public MonthDataParser(String type, int year, int month) throws IOException {
		this.type = type;
		this.year = year;
		this.month = month;
		this.initFile();
	}
	
	private final String type;
	
	private final int year;
	
	private final int month;
	
	private void initFile() throws IOException {
		File f = new File(this.getFolder());
		f.mkdirs();
		f = new File(this.getFileName());
		if (!f.exists()) {
			f.createNewFile();
			int nbDays = this.getNbDays();
			PrintWriter out = new PrintWriter(this.getFileName());
			for (int i = 0; i < nbDays+1; i++) {
				out.print("*\r\n");
			}
			out.close();
		}
	}
	
	protected int getNbDays() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, month-1);
		return cal.getMaximum(Calendar.DATE);
	}
	
	abstract void update() throws IOException;
	
	protected String getFolder() {
		return "src/dataBase/".concat(this.type).concat("/").concat(Integer.toString(this.year).concat("/").concat(this.extendString(this.month, 2)));
	}
	
	protected String getFileName() {
		return this.getFolder().concat("/month.txt");
	}
	
	private String extendString(int value, int length) {
		String result = Integer.toString(value);
		if (result.length() > length) {
			return result.substring(result.length()-length, result.length());
		}
		for (int i = 1; i <= length; i++) {
			if (result.length() < i) {
				result = "0".concat(result);
			}
		}
		return result;
	}
}