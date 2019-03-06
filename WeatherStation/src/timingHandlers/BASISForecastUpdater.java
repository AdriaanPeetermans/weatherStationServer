package timingHandlers;

import basis.BasisHandler;
import exceptions.BasisException;
import exceptions.OWMException;
import openWeatherMap.Connector;
import openWeatherMap.ForecastParser;
import openWeatherMap.WeatherData;
import openWeatherMap.WeatherDataParser;
import timingHandlers.helpers.DayTime;

public class BASISForecastUpdater extends Updater {
	
	public BASISForecastUpdater(String ip, int port, int minutesInterval) {
		super(ip, port, minutesInterval);
		this.basisHandler = new BasisHandler(this.ip, this.port, 10, 5, 5);
		this.forecastParser = new ForecastParser();
		this.weatherDataParser = new WeatherDataParser();
	}
	
	public BASISForecastUpdater(String ip, int port, DayTime when) {
		super (ip, port, when);
		this.basisHandler = new BasisHandler(this.ip, this.port, 10, 5, 5);
		this.forecastParser = new ForecastParser();
		this.weatherDataParser = new WeatherDataParser();
	}
	
	private final BasisHandler basisHandler;
	
	private final ForecastParser forecastParser;
	
	private final WeatherDataParser weatherDataParser;
	
	public void run() {
		super.run();
//		Connector connector = new Connector("886705b4c1182eb1c69f28eb8c520e20", "Tessenderlo,BE", (byte) 10);
//		String[] forecast = new String[10];
//		try {
//			forecast = connector.getWeatherData();
//		}
//		catch (OWMException e) {
//			e.printStackTrace();
//		}
//		WeatherData[] data = new WeatherData[10];
//		for (int i = 0; i < 10; i++) {
//			data[i] = this.weatherDataParser.parse(forecast[i]);
//		}
//		this.forecastParser.writeData(data);
		WeatherData[] data = this.forecastParser.parseFile();
		try {
			this.basisHandler.sendForecast(data);
		}
		catch (BasisException e) {
			e.printStackTrace();
		}
	}
}
