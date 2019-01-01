package testers;

import openWeatherMap.ForecastParser;
import openWeatherMap.WeatherData;

public class ForecastParser_tb {

	public static void main(String[] args) {
		ForecastParser parser = new ForecastParser("/Users/adriaanpeetermans/Documents/workspace/WeatherStation/src/openWeatherMap/forecast.txt");
		WeatherData[] data = parser.parseFile();
		parser.writeData(data);
	}

}
