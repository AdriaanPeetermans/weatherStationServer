package testers;

import java.util.HashSet;

import dataBase.HardDriveParser;
import dataBase.helpers.DateObject;

public class HardDriveParser_tb {

	public static void main(String[] args) {
		HardDriveParser parser = new HardDriveParser();
		HashSet<DateObject> dates = parser.getDates();
		for (DateObject date : dates) {
			System.out.println(date);
		}
		parser.removeDate(new DateObject(1, 6, 19, "BASIS"));
		//parser.appendDate(new DateObject(5, 6, 19, "SENSOR1"));
		dates = parser.getDates();
		for (DateObject date : dates) {
			System.out.println(date);
		}
	}

}
