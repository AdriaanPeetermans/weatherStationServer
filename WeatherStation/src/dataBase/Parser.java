package dataBase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import dataBase.helpers.BasisData;
import dataBase.helpers.DataObject;
import dataBase.helpers.Sensor1Data;

public class Parser {
	
	public Parser(String type, int year, int month, int day) {
		this.type = type;
		this.year = year;
		this.month = month;
		this.day = day;
		File f = new File("src/dataBase/".concat(this.type).concat("/").concat(Integer.toString(this.year).concat("/").concat(this.extendString(this.month, 2).concat("/")).concat(this.extendString(this.day, 2)).concat(".txt")));
		try {
			f.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Parser(String type, int year, int month, int day, boolean onlyRead) {
		this.type = type;
		this.year = year;
		this.month = month;
		this.day = day;
		if (!onlyRead) {
			File f = new File("src/dataBase/".concat(this.type).concat("/").concat(Integer.toString(this.year).concat("/").concat(this.extendString(this.month, 2).concat("/")).concat(this.extendString(this.day, 2)).concat(".txt")));
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public final String type;
	
	public final int year;
	
	public final int month;
	
	public final int day;
	
	public String giveFileName() {
		return "src/dataBase/".concat(this.type).concat("/").concat(Integer.toString(this.year).concat("/").concat(this.extendString(this.month, 2).concat("/")).concat(this.extendString(this.day, 2)).concat(".txt"));
	}
	
	public void writeToFile(String data) {
		try {
			PrintWriter out = new PrintWriter(this.giveFileName());
			out.print(data);
			out.close();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
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
	
	public DataObject readFile() {
		DataObject result;
		switch (this.type) {
			case "BASIS":
				result = new BasisData(this.day, this.month, this.year);
				break;
			case "SENSOR1":
				result = new Sensor1Data(this.day, this.month, this.year);
				break;
			default:
				result = null;
		}
		result.parse();
		return result;
	}
}
