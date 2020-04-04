package dataBase;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import dataBase.helpers.BasisData;
import dataBase.helpers.DataObject;
import dataBase.helpers.Sensor1Data;
import dataBase.helpers.Sensor2Data;

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
	
	public void appendFile(String data) {
		try (FileWriter fw = new FileWriter(this.giveFileName(), true);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter out = new PrintWriter(bw)) {
			out.print(data);
		}
		catch (IOException e) {
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
	
	public ArrayList<String> readLines() {
		DataObject dataObject;
		switch (this.type) {
			case "BASIS":
				dataObject = new BasisData(this.day, this.month, this.year);
				break;
			case "SENSOR1":
				dataObject = new Sensor1Data(this.day, this.month, this.year);
				break;
			case "SENSOR2":
				dataObject = new Sensor2Data(this.day, this.month, this.year);
				break;
			default:
				dataObject = null;
				break;
		}
		return dataObject.getLines();
	}
	
	public String getSize() {
		DataObject dataObject;
		switch (this.type) {
			case "BASIS":
				dataObject = new BasisData(this.day, this.month, this.year);
				break;
			case "SENSOR1":
				dataObject = new Sensor1Data(this.day, this.month, this.year);
				break;
			case "SENSOR2":
				dataObject = new Sensor2Data(this.day, this.month, this.year);
				break;
			default:
				dataObject = null;
				break;
		}
		return dataObject.getSize();
	}
}
