package dataBase.helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class Sensor2Data extends DataObject {
	
	public Sensor2Data(int day, int month, int year) {
		super("SENSOR2", day, month, year);
	}
	
	public ArrayList<Integer> light = new ArrayList<Integer>();
	
	public ArrayList<Float> moisture = new ArrayList<Float>();
	
	public ArrayList<Float> temperature = new ArrayList<Float>();
	
	public ArrayList<Float> soilTempR = new ArrayList<Float>();
	
	public ArrayList<Float> soilTempL = new ArrayList<Float>();
	
	public ArrayList<Float> soilMoistR = new ArrayList<Float>();
	
	public ArrayList<Float> soilMoistL = new ArrayList<Float>();
	
	public ArrayList<Integer> polarity = new ArrayList<Integer>();

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
	
	private void parseLine(String line) {
		String[] parts = line.split(",");
		this.light.add(Integer.parseInt(parts[0]));
		this.moisture.add(Float.parseFloat(parts[1]));
		this.temperature.add(Float.parseFloat(parts[2]));
		this.soilTempR.add(Float.parseFloat(parts[3]));
		this.soilTempL.add(Float.parseFloat(parts[4]));
		this.soilMoistR.add(Float.parseFloat(parts[5]));
		this.soilMoistL.add(Float.parseFloat(parts[6]));
		parts = parts[7].split(" ");
		this.polarity.add(Integer.parseInt(parts[0]));
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
	
	public void adjustData(int minutes, int startMinute) {
		//Soil data should still be fixed!
		this.fixDataInt(this.light, 20, 0, 1024);
		this.adjustInt(this.light, minutes, startMinute);
		this.fixDataFloat(this.moisture, 30, 0, 100);
		this.adjustFloat(this.moisture, minutes, startMinute);
		this.fixDataFloat(this.temperature, 30, -30, 50);
		this.adjustFloat(this.temperature, minutes, startMinute);
		//this.fixDataFloat(this.soilTempR, 100, -100, 100);
		this.adjustFloat(this.soilTempR, minutes, startMinute);
		//this.fixDataFloat(this.soilTempL, 100, -100, 100);
		this.adjustFloat(this.soilTempL, minutes, startMinute);
		//this.fixDataFloat(this.soilMoistR, 100, -100, 100);
		this.adjustFloat(this.soilMoistR, minutes, startMinute);
		//this.fixDataFloat(this.soilMoistL, 100, -100, 100);
		this.adjustFloat(this.soilMoistL, minutes, startMinute);
		this.adjustInt(this.polarity, minutes, startMinute);
		this.adjustTime(minutes, startMinute);
	}

	public void fixData() {
		this.fixDataInt(this.light, 20, 0, 1024);
		this.fixDataFloat(this.moisture, 30, 0, 100);
		this.fixDataFloat(this.temperature, 30, -30, 50);
		//Soil data should still be fixed!
	}

}
