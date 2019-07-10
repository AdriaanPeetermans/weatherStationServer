package dataBase;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import dataBase.helpers.BasisData;
import dataBase.helpers.Sensor1Data;

public class MonthSensor1 extends MonthDataParser {

	public MonthSensor1(int year, int month) throws IOException {
		super("SENSOR1", year, month);
	}

	@Override
	void update() throws IOException {
		this.fillData();
	}
	
	private void fillData() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(this.getFileName()));
		int maxDays = this.getNbDays();
		for (int i = 0; i < maxDays; i++) {
			String line = reader.readLine();
			System.out.println(line);
			if (line.equals("*")) {
				String updatedLine = this.getUpdatedLine(i+1);
			}
			line = reader.readLine();
		}
		reader.close();
	}
	
	/*
	 * Line contents:
	 * 	maxTemp#minTemp#meanTemp#maxTempTime#minTempTime#maxMoist#minMoist#meanMoist#maxMoistTime#minMoistTime#maxPress#minPress#meanPress#
	 */
	private String getUpdatedLine(int day) {
		Parser sensor1Parser = new Parser(this.type, this.year, this.month, day, true);
		Sensor1Data sensor1Data = (Sensor1Data) sensor1Parser.readFile();
		
	}
	
//	private final ArrayList<Sensor1Data> data = new ArrayList<Sensor1Data>(this.getNbDays());
	
//	private class Sensor1Data {
//		
//		public Sensor1Data(int light, float moist, float temp, float press, float PV, float BV, Calendar cal) {
//			this.light = light;
//			this.moist = moist;
//			this.temp = temp;
//			this.press = press;
//			this.PV = PV;
//			this.BV = BV;
//			this.cal = cal;
//		}
//		
//		public final int light;
//		
//		public final float moist;
//		
//		public final float temp;
//		
//		public final float press;
//		
//		public final float PV;
//		
//		public final float BV;
//		
//		public final Calendar cal;
//	}
}
