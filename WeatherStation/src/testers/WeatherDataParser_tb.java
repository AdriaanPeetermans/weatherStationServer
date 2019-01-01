package testers;

import openWeatherMap.WeatherData;
import openWeatherMap.WeatherDataParser;

public class WeatherDataParser_tb {
	
	public static void main(String[] args) {
		WeatherDataParser parser = new WeatherDataParser();
		WeatherData data = parser.parse("{\"dt\":1541761200,\"rain\":1.3,\"temp\":{\"min\":8.16,\"max\":12.97,\"eve\":10.45,\"night\":8.16,\"day\":12.97,\"morn\":9.14},\"deg\":302,\"weather\":[{\"icon\":\"10d\",\"description\":\"light rain\",\"main\":\"Rain\",\"id\":500}],\"humidity\":0,\"pressure\":1019.85,\"clouds\":93,\"speed\":1.64}");
		System.out.println(data.clouds);
		System.out.println(data.description);
		System.out.println(data.rain);
		System.out.println(data.tempDay);
	}
}
