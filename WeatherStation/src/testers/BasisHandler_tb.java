package testers;

import basis.BasisHandler;
import exceptions.BasisException;

public class BasisHandler_tb {

	public static void main(String[] args) {
		BasisHandler bh = new BasisHandler("192.168.1.58", 9876, 10, 5, 1);
		//BasisHandler bh = new BasisHandler("127.0.0.1", 9876, 10, 5, 1);
		//ForecastParser parser = new ForecastParser();
		//WeatherData[] data = parser.parseFile();
		//System.out.println(bh.createTextForecast(data[0]));
		try {
			//bh.setTime();
			//System.out.println(bh.getDayIndex());
			bh.transferFiles(8);
			//bh.setDayCounter(8);
			//System.out.println(bh.getBasisToPiData().toString());
		} catch (BasisException e) {
			e.printStackTrace();
		}
	}

}
