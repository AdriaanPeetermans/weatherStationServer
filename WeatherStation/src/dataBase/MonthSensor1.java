package dataBase;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

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
		String line = reader.readLine();
		while (line != null) {
			System.out.println(line);
			if 
			line = reader.readLine();
		}
		reader.close();
	}
	
	private final ArrayList<Sensor1Data> data = new ArrayList<Sensor1Data>(this.getNbDays());
	
	private class Sensor1Data {
		
		public Sensor1Data(int light, float moist, float temp, float press, float PV, float BV, Calendar cal) {
			this.light = light;
			this.moist = moist;
			this.temp = temp;
			this.press = press;
			this.PV = PV;
			this.BV = BV;
			this.cal = cal;
		}
		
		public final int light;
		
		public final float moist;
		
		public final float temp;
		
		public final float press;
		
		public final float PV;
		
		public final float BV;
		
		public final Calendar cal;
	}
}
