package openWeatherMap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ForecastParser {
	
	public ForecastParser(String file) {
		this.file = file;
	}
	
	public ForecastParser() {
		this("/Users/adriaanpeetermans/Documents/workspace/WeatherStation/src/openWeatherMap/forecast.txt");
	}
	
	public final String file;
	
	public WeatherData[] parseFile() {
		String[] result = new String[10];
		File dataFile = new File(this.file);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(dataFile));
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String text;
		try {
			text = reader.readLine();
			int index = 0;
			while (text != null) {
				result[index] = text;
				index ++;
				text = reader.readLine();
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return this.readText(result);
	}
	
	public void writeData(WeatherData[] data) {
		String[] text = new String[data.length];
		for (int i = 0; i < data.length; i++) {
			text[i] = this.createJSON(data[i]);
		}
		File dataFile = new File(this.file);
		try {
		    FileWriter writer = new FileWriter(dataFile, false);
		    for (int i = 0; i < data.length; i++) {
		    	writer.write(text[i].concat("\n"));
		    }
		    writer.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}           
	}
	
	private String createJSON(WeatherData data) {
		String result = "{";
		result = result.concat(this.addField("dt", data.dt));
		result = result.concat(",");
		result = result.concat(this.addField("rain", data.rain));
		result = result.concat(",");
		String resultTemp = "{";
		resultTemp = resultTemp.concat(this.addField("min", data.tempMin));
		resultTemp = resultTemp.concat(",");
		resultTemp = resultTemp.concat(this.addField("max", data.tempMax));
		resultTemp = resultTemp.concat(",");
		resultTemp = resultTemp.concat(this.addField("eve", data.tempEve));
		resultTemp = resultTemp.concat(",");
		resultTemp = resultTemp.concat(this.addField("night", data.tempNight));
		resultTemp = resultTemp.concat(",");
		resultTemp = resultTemp.concat(this.addField("day", data.tempDay));
		resultTemp = resultTemp.concat(",");
		resultTemp = resultTemp.concat(this.addField("morn", data.tempMorn));
		resultTemp = resultTemp.concat("}");
		result = result.concat(this.addField("temp", resultTemp));
		result = result.concat(",");
		result = result.concat(this.addField("deg", data.windDeg));
		result = result.concat(",");
		resultTemp = "[{";
		resultTemp = resultTemp.concat(this.addField("icon", "\"".concat(data.icon).concat("\"")));
		resultTemp = resultTemp.concat(",");
		resultTemp = resultTemp.concat(this.addField("description", "\"".concat(data.description).concat("\"")));
		resultTemp = resultTemp.concat(",");
		resultTemp = resultTemp.concat(this.addField("main", "\"".concat(data.main).concat("\"")));
		resultTemp = resultTemp.concat(",");
		resultTemp = resultTemp.concat(this.addField("id", data.id));
		resultTemp = resultTemp.concat("}]");
		result = result.concat(this.addField("weather", resultTemp));
		result = result.concat(",");
		result = result.concat(this.addField("humidity", data.humidity));
		result = result.concat(",");
		result = result.concat(this.addField("pressure", data.pressure));
		result = result.concat(",");
		result = result.concat(this.addField("clouds", data.clouds));
		result = result.concat(",");
		result = result.concat(this.addField("speed", data.windSpeed));
		result = result.concat("}");
		return result;
	}
	
	private String addField(String name, Object value) {
		String result = "\"";
		result = result.concat(name);
		result = result.concat("\":");
		result = result.concat(value.toString());
		return result;
	}
	
	private WeatherData[] readText(String[] text) {
		WeatherData[] result = new WeatherData[text.length];
		WeatherDataParser parser = new WeatherDataParser();
		for (int i = 0; i < text.length; i++) {
			result[i] = parser.parse(text[i]);
		}
		return result;
	}

}
