package openWeatherMap;

import org.json.JSONException;
import org.json.JSONObject;

public class WeatherDataParser {
	
	public WeatherDataParser() {
		
	}
	
	public WeatherData parse(String data) {
		long dt = 0;
		float tempMin = 0;
		float tempMax = 0;
		float tempEve = 0;
		float tempNight = 0;
		float tempDay = 0;
		float tempMorn = 0;
		int windDeg = 0;
		String icon = null;
		String description = null;
		String main = null;
		int id = 0;
		float humidity = 0;
		float pressure = 0;
		float clouds = 0;
		float windSpeed = 0;
		float rain = 0;
		try {
			JSONObject obj = new JSONObject(data);
			dt = obj.getLong("dt");
			tempMin = (float) obj.getJSONObject("temp").getDouble("min");
			tempMax = (float) obj.getJSONObject("temp").getDouble("max");
			tempEve = (float) obj.getJSONObject("temp").getDouble("eve");
			tempNight = (float) obj.getJSONObject("temp").getDouble("night");
			tempDay = (float) obj.getJSONObject("temp").getDouble("day");
			tempMorn = (float) obj.getJSONObject("temp").getDouble("morn");
			windDeg = obj.getInt("deg");
			icon = obj.getJSONArray("weather").getJSONObject(0).getString("icon");
			description = obj.getJSONArray("weather").getJSONObject(0).getString("description");
			main = obj.getJSONArray("weather").getJSONObject(0).getString("main");
			id = obj.getJSONArray("weather").getJSONObject(0).getInt("id");
			humidity = (float) obj.getDouble("humidity");
			pressure = (float) obj.getDouble("pressure");
			clouds = (float) obj.getDouble("clouds");
			windSpeed = (float) obj.getDouble("speed");
			rain = (float) obj.getDouble("rain");
		}
		catch (JSONException e) {
			//e.printStackTrace();
		}
		return new WeatherData(dt, tempMin, tempMax, tempEve, tempNight, tempDay, tempMorn, windDeg, icon, description, main, id, humidity, pressure, clouds, windSpeed, rain);
		
	}

}
