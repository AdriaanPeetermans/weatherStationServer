package basis;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import dataBase.DataBaseManager;
import dataBase.Parser;
import exceptions.BasisException;
import exceptions.OWMException;
import openWeatherMap.Connector;
import openWeatherMap.ForecastParser;
import openWeatherMap.WeatherData;
import openWeatherMap.WeatherDataParser;

public class SingleMessageConnector {
	
	public SingleMessageConnector(String basisIP, int basisPort, int timeInt, int forecastInt, int BTPInt) {
		this.basisIP = basisIP;
		this.basisPort = basisPort;
		this.timeInt = timeInt*60*1000;			//Millis
		this.forecastInt = forecastInt*60*1000;
		this.BTPInt = BTPInt*60*1000;
		long now = System.currentTimeMillis();
		this.timeDead = now;
		this.forecastDead = now;
		this.BTPDead = now;
		this.forecastParser = new ForecastParser();
		this.weatherDataParser = new WeatherDataParser();
		this.basisHandler = new BasisHandler(this.basisIP, this.basisPort, 10, 5, 5);
	}
	
	private final String basisIP;
	
	private final int basisPort;
	
	private final int timeInt;
	
	private long timeDead;
	
	private final int forecastInt;
	
	private long forecastDead;
	
	private final int BTPInt;
	
	private long BTPDead;
	
	private final ForecastParser forecastParser;
	
	private final WeatherDataParser weatherDataParser;
	
	private final BasisHandler basisHandler;
	
	/**
	 * Message will contain concatenation of different messages of type:
	 * 		P00:	Time adjustment
	 * 		P02:	Forecast adjustment
	 * 		P05:	Get BTP data
	 * 		100:	SENSOR1 data
	 * 		200:	SENSOR2 data
	 * 
	 * Message structure:
	 * 		S00 numberMess@mess0@ ... @messn@
	 * Answer structure:
	 * 		B00 numberAnswer@answer0@ ... @answern@
	 * 
	 * @param sensorMessage:	method is fired upon receiving a sensor data message.
	 */
	public String sendSingleMessage(String sensorMessage) {
		ArrayList<String> parts = new ArrayList<String>(5);
		parts.add(sensorMessage);
		long now = System.currentTimeMillis();
		if (now > this.timeDead) {
			String message = this.constructTimeMessage();
			parts.add(message);
			this.timeDead += this.timeInt;
		}
		if (now > this.forecastDead) {
			String message = this.constructForecastMessage();
			parts.add(message);
			this.forecastDead += this.forecastInt;
		}
		if (now > this.BTPDead) {
			String message = this.constructBTPMessage();
			parts.add(message);
			this.BTPDead += this.BTPInt;
		}
		String message = "S00 ".concat(Integer.toString(parts.size())).concat("@");
		for (String part : parts) {
			message = message.concat(part).concat("@");
		}
		message = message.concat("\r\n");
		String sensorAnswer = "ERROR";
		try {
			System.out.println("Send to basis: ".concat(message));
			String answer = this.basisHandler.sendToBasis(message);
			answer = answer.substring(4);
			String[] answerParts = answer.split("@");
			int numberAnswers = Integer.parseInt(answerParts[0]);
			for (int i = 0; i < numberAnswers; i++) {
				this.handleAnswer(answerParts[i+1]);
				if (answerParts[i+1].substring(0, 3).equals("B00")) {
					sensorAnswer = answerParts[i+1];
				}
			}
		}
		catch (BasisException e) {
			e.printStackTrace();
		}
		return sensorAnswer;
	}
	
	/**
	 * Possible answers:
	 * 		B05 numberLines#line0#line1# ... #linen#
	 */
	private void handleAnswer(String answer) {
		String type = answer.substring(0, 3);
		answer = answer.substring(4);
		if (type.equals("B05")) {
			String[] parts = answer.split("#");
			int number = Integer.parseInt(parts[0]);
			ArrayList<String> lines = new ArrayList<String>(number);
			for (int i = 0; i < number; i++) {
				lines.add(parts[i+1]);
			}
			DataBaseManager dbManager = new DataBaseManager("BASIS");
			for (int i = lines.size()-1; i >= 0; i--) {
				int lastDays = SingleMessageConnector.getLastDays(lines.get(i).split(" ")[2]);
				Parser parser = dbManager.addFolder(lastDays);
				parser.appendFile(lines.get(i).concat("\r\n"));
			}
		}
	}
	
	private static int getLastDays(String date) {
		String[] parts = date.split("/");
		int day = Integer.parseInt(parts[0]);
		int month = Integer.parseInt(parts[1]);
		int year = 2000+Integer.parseInt(parts[2]);
		Calendar now = Calendar.getInstance();
		Calendar cal = (Calendar) now.clone();
		cal.set(Calendar.DATE, day);
		cal.set(Calendar.MONTH, month-1);
		cal.set(Calendar.YEAR, year);
		return (int) TimeUnit.MILLISECONDS.toDays(Math.abs(now.getTimeInMillis() - cal.getTimeInMillis()));
	}
	
	private String constructTimeMessage() {
		Calendar cal = Calendar.getInstance();
		int dayIndex = cal.get(Calendar.DAY_OF_WEEK);
		dayIndex = dayIndex - 2;
		if (dayIndex < 0) {
			dayIndex = dayIndex + 7;
		}
		int minute = cal.get(Calendar.MINUTE);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int day = cal.get(Calendar.DATE);
		int month = cal.get(Calendar.MONTH);
		month ++;
		int year = cal.get(Calendar.YEAR);
		year = year - 2000;
		String message = "P00 ";
		message = message.concat(String.format("%02d", hour));
		message = message.concat(String.format("%02d", minute));
		message = message.concat(" ");
		message = message.concat(String.format("%01d", dayIndex));
		message = message.concat(String.format("%02d", day));
		message = message.concat(String.format("%02d", month));
		message = message.concat(String.format("%02d", year));
		return message;
	}
	
	private String constructForecastMessage() {
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
		String message = "P02 ";
		message = message.concat(String.format("%02d", 2));
		for (int i = 0; i < 2; i++) {
			message = message.concat(",");
			message = message.concat(this.createTextForecast(data[i]));
		}
		return message;
	}
	
	private String createTextForecast(WeatherData data) {
		String result = "{";
		result = result.concat(String.format("%03d", (int) (data.tempMorn*10)));
		result = result.concat(",");
		result = result.concat(String.format("%03d", (int) (data.tempDay*10)));
		result = result.concat(",");
		result = result.concat(String.format("%03d", (int) (data.tempEve*10)));
		result = result.concat(",");
		result = result.concat(String.format("%03d", (int) (data.tempNight*10)));
		result = result.concat(",");
		result = result.concat(String.format("%03d", (int) (data.tempMax*10)));
		result = result.concat(",");
		result = result.concat(String.format("%03d", (int) (data.tempMin*10)));
		result = result.concat(",");
		result = result.concat(data.icon);
		result = result.concat(",");
		result = result.concat(String.format("%02d", data.description.length()));
		result = result.concat(",");
		result = result.concat(data.description);
		result = result.concat(",");
		result = result.concat(String.format("%04d", (int) (data.humidity*10)));
		result = result.concat(",");
		result = result.concat(String.format("%06d", (int) (data.pressure*100)));
		result = result.concat(",");
		result = result.concat(String.format("%04d", (int) (data.clouds*10)));
		result = result.concat(",");
		result = result.concat(String.format("%04d", (int) (data.windSpeed*100)));
		result = result.concat(",");
		result = result.concat(String.format("%03d", data.windDeg));
		result = result.concat(",");
		result = result.concat(String.format("%04d", (int) (data.rain*100)));
		result = result.concat("}");
		return result;
	}
	
	private String constructBTPMessage() {
		return "P05";
	}
}
