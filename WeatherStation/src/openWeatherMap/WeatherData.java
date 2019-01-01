package openWeatherMap;

public class WeatherData {
	
	WeatherData(long dt, float tempMin, float tempMax, float tempEve, float tempNight, float tempDay, float tempMorn, int windDeg, String icon, String description, String main, int id, float humidity, float pressure, float clouds, float windSpeed, float rain) {
		this.dt = dt;
		this.tempMin = tempMin;
		this.tempMax = tempMax;
		this.tempEve = tempEve;
		this.tempNight = tempNight;
		this.tempDay = tempDay;
		this.tempMorn = tempMorn;
		this.windDeg = windDeg;
		this.icon = icon;
		this.description = description;
		this.main = main;
		this.id = id;
		this.humidity = humidity;
		this.pressure = pressure;
		this.clouds = clouds;
		this.windSpeed = windSpeed;
		this.rain = rain;
	}
	
	WeatherData(long dt, float tempMin, float tempMax, float tempEve, float tempNight, float tempDay, float tempMorn, int windDeg, String icon, String description, String main, int id, float humidity, float pressure, float clouds, float windSpeed) {
		this(dt, tempMin, tempMax, tempEve, tempNight, tempDay, tempMorn, windDeg, icon, description, main, id, humidity, pressure, clouds, windSpeed, 0);
	}
	
	public final long dt;
	
	public final float tempMin;
	
	public final float tempMax;
	
	public final float tempEve;
	
	public final float tempNight;
	
	public final float tempDay;
	
	public final float tempMorn;
	
	public final int windDeg;
	
	public final String icon;
	
	public final String description;
	
	public final String main;
	
	public final int id;
	
	public final float humidity;
	
	public final float pressure;
	
	public final float clouds;
	
	public final float windSpeed;
	
	public final float rain;
}
