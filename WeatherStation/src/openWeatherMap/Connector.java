package openWeatherMap;

import java.io.IOException;

import org.json.JSONException;

import exceptions.OWMException;
import net.aksingh.owmjapis.DailyForecast;
import net.aksingh.owmjapis.OpenWeatherMap;

public class Connector {
	
	public Connector(String key, String city, byte forecastDays) {
		this.key = key;
		this.city = city;
		this.forecastDays = forecastDays;
	}
	
	private final String key;
	
	private final String city;
	
	private final byte forecastDays;
	
	public String[] getWeatherData() throws OWMException {
		String[] result = new String[this.forecastDays];
		OpenWeatherMap.Units units = OpenWeatherMap.Units.METRIC;
		OpenWeatherMap owm = new OpenWeatherMap(units, this.key);
		try {
			DailyForecast forecast = owm.dailyForecastByCityName(this.city, forecastDays);
			int numForecasts = forecast.getForecastCount();
			for (int i = 0; i < numForecasts; i++) {
				DailyForecast.Forecast dayForecast = forecast.getForecastInstance(i);
				result[i] = dayForecast.getRawResponse();
			}
			
		}
		catch (IOException | JSONException e) {
			throw new OWMException("No connection to OWM!");
		}
		
		return result;
	}
}
