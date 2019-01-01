package dataBase;

import java.io.File;
import java.util.Calendar;

public class DataBaseManager {
	
	public DataBaseManager(String type) {
		this.type = type;
		File f = new File("src/dataBase/".concat(type));
		f.mkdir();
	}
	
	private final String type;
	
	public Parser addFolder(int lastDays) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR,-lastDays);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH)+1;
		int day = cal.get(Calendar.DATE);
		File f = new File("src/dataBase/".concat(this.type).concat("/").concat(Integer.toString(year)).concat("/").concat(this.extendString(month, 2)));
		f.mkdirs();
		return new Parser(this.type, year, month, day);
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
