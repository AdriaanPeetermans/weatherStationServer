package testers;

import dataBase.helpers.Sensor1Data;

public class Sensor1Data_tb {

	public static void main(String[] args) {
		Sensor1Data reader = new Sensor1Data("SENSOR1", 14, 11, 2018);
		reader.parse();
		System.out.println(reader.time.get(0));
		System.out.println(reader.light.get(0));
		System.out.println(reader.moisture.get(0));
		System.out.println(reader.light.size());
		System.out.println(reader.light.get(275));
		System.out.println(reader.time.get(275));
	}

}
