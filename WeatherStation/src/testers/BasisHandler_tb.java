package testers;

import java.util.concurrent.TimeUnit;

import basis.BasisHandler;
import basis.helpers.BasisLiveData;
import exceptions.BasisException;

public class BasisHandler_tb {

	public static void main(String[] args) {
		BasisHandler bh = new BasisHandler("192.168.1.61", 9876, 10, 5, 1);
		//ForecastParser parser = new ForecastParser();
		//WeatherData[] data = parser.parseFile();
		//System.out.println(bh.createTextForecast(data[0]));
		try {
			//bh.setTime();
			//System.out.println(bh.getDayIndex());
			bh.transferFiles(8);
		} catch (BasisException e) {
			e.printStackTrace();
		}
	}

}
