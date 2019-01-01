package testers;

import java.util.Calendar;

import dataBase.helpers.BasisData;

public class DataObject_tb {

	public static void main(String[] args) {
		BasisData data = new BasisData(21, 12, 2018);
		data.parse();
		for (int i = 0; i < 5; i++) {
			System.out.println(Integer.toString(data.time.get(i).get(Calendar.HOUR_OF_DAY)).concat(":").concat(Integer.toString(data.time.get(i).get(Calendar.MINUTE))));
			System.out.println(data.temperature.get(i));
		}
		System.out.println("#####################");
		data.adjustData(60);
		for (int i = 0; i < 5; i++) {
			System.out.println(Integer.toString(data.time.get(i).get(Calendar.HOUR_OF_DAY)).concat(":").concat(Integer.toString(data.time.get(i).get(Calendar.MINUTE))));
			System.out.println(data.temperature.get(i));
		}
	}

}
