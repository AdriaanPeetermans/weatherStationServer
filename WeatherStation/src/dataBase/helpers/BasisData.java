package dataBase.helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class BasisData extends DataObject {
	
	public BasisData(int day, int month, int year) {
		super("BASIS", day, month, year);
	}
	
	public ArrayList<Integer> light = new ArrayList<Integer>();
	
	public ArrayList<Float> moisture = new ArrayList<Float>();
	
	public ArrayList<Float> temperature = new ArrayList<Float>();

	@Override
	public void parse() {
		try {
			File file = new File("src/dataBase/".concat(this.type).concat("/").concat(Integer.toString(this.year).concat("/").concat(this.extendString(this.month, 2).concat("/")).concat(this.extendString(this.day, 2)).concat(".txt")));
			if (!file.exists()) {
				return;
			}
			this.exist = true;
			BufferedReader br = new BufferedReader(new FileReader(file));
			String st = br.readLine();
			while (st != null) {
				this.parseLine(st);
				st = br.readLine();
			}
			br.close();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void adjustData(int minutes, int startMinute) {
		this.fixDataFloat(this.temperature, 30, -30, 50);
		this.adjustFloat(this.temperature, minutes, startMinute);
		this.fixDataFloat(this.moisture, 30, 0, 100);
		this.adjustFloat(this.moisture, minutes, startMinute);
		this.fixDataInt(this.light, 20, 0, 1024);
		this.adjustInt(this.light, minutes, startMinute);
		this.adjustTime(minutes, startMinute);
	}
	
	public void fixData() {
		this.fixDataFloat(this.temperature, 30, -30, 50);
		this.fixDataFloat(this.moisture, 30, 0, 100);
		this.fixDataInt(this.light, 20, 0, 1024);
	}
	
	private void parseLine(String line) {
		String[] parts = line.split(",");
		this.temperature.add(Float.parseFloat(parts[0]));
		this.moisture.add(Float.parseFloat(parts[1]));
		parts = parts[2].split(" ");
		this.light.add(Integer.parseInt(parts[0]));
		String[] inDay = parts[1].split(":");
		String[] inYear = parts[2].split("/");
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DATE, Integer.parseInt(inYear[0]));
		cal.set(Calendar.MONTH, Integer.parseInt(inYear[1])-1);
		cal.set(Calendar.YEAR, Integer.parseInt(inYear[2])+2000);
		cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(inDay[0]));
		cal.set(Calendar.MINUTE, Integer.parseInt(inDay[1]));
		cal.set(Calendar.SECOND, Integer.parseInt(inDay[2]));
		this.time.add(cal);
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
