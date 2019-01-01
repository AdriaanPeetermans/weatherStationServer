package basis;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Calendar;

import basis.helpers.BasisLiveData;
import dataBase.DataBaseManager;
import dataBase.Parser;
import exceptions.BasisException;
import openWeatherMap.WeatherData;

public class BasisHandler {
	
	public BasisHandler(String ip, int port, int maxDays, int maxErrors, int liveDataMinutes) {
		this.ip = ip;
		this.port = port;
		this.maxDays = maxDays;
		this.maxErrors = maxErrors;
		this.liveDataMinutes = liveDataMinutes;
	}
	
	public final String ip;
	
	public final int port;
	
	public int maxDays;
	
	private int errorCounter = 0;
	
	public final int maxErrors;
	
	public final int liveDataMinutes;
	
	public BasisLiveData liveData = null;
	
	public void setTime() throws BasisException {
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
		message = message.concat(this.extendString(hour, 2));
		message = message.concat(this.extendString(minute, 2));
		message = message.concat(" ");
		message = message.concat(this.extendString(dayIndex, 1));
		message = message.concat(this.extendString(day, 2));
		message = message.concat(this.extendString(month, 2));
		message = message.concat(this.extendString(year, 2));
		String answer = this.sendToBasis(message);
		if (!answer.equals("B00 OK")) {
			throw new BasisException("Wrong answer from basis, answer: ".concat(answer));
		}
	}
	
	/**
	 * Forecast structure:
	 * 		P02 numberDays(2),{day1},{day2},...,{dayn}
	 * 
	 * Day structure:
	 * 		tempMorn*10(3),tempDay*10(3),tempEve*10(3),tempNight*10(3),tempMax*10(3),tempMin*10(3),icon(3),descriptionLength(2),description(descriptionLength),humidity*10(4),pressure*100(6),clouds*10(4),speed*100(4),deg(3),rain*100(4)
	 * 
	 * Answer:
	 * 		B02 OK
	 */
	public void sendForecast(WeatherData[] forecast) throws BasisException {
		String message = "P02 ";
		message = message.concat(this.extendString(forecast.length, 2));
		for (int i = 0; i < forecast.length; i++) {
			message = message.concat(",");
			message = message.concat(this.createTextForecast(forecast[i]));
		}
		String answer = this.sendToBasis(message);
		if (!answer.equals("B02 OK")) {
			throw new BasisException("Wrong answer from basis, answer: ".concat(answer));
			
		}
	}
	
	private String createTextForecast(WeatherData data) {
		String result = "{";
		result = result.concat(this.extendString((int) (data.tempMorn*10), 3));
		result = result.concat(",");
		result = result.concat(this.extendString((int) (data.tempDay*10), 3));
		result = result.concat(",");
		result = result.concat(this.extendString((int) (data.tempEve*10), 3));
		result = result.concat(",");
		result = result.concat(this.extendString((int) (data.tempNight*10), 3));
		result = result.concat(",");
		result = result.concat(this.extendString((int) (data.tempMax*10), 3));
		result = result.concat(",");
		result = result.concat(this.extendString((int) (data.tempMin*10), 3));
		result = result.concat(",");
		result = result.concat(data.icon);
		result = result.concat(",");
		result = result.concat(this.extendString(data.description.length(), 2));
		result = result.concat(",");
		result = result.concat(data.description);
		result = result.concat(",");
		result = result.concat(this.extendString((int) (data.humidity*10), 4));
		result = result.concat(",");
		result = result.concat(this.extendString((int) (data.pressure*100), 6));
		result = result.concat(",");
		result = result.concat(this.extendString((int) (data.clouds*10), 4));
		result = result.concat(",");
		result = result.concat(this.extendString((int) (data.windSpeed*100), 4));
		result = result.concat(",");
		result = result.concat(this.extendString(data.windDeg, 3));
		result = result.concat(",");
		result = result.concat(this.extendString((int) (data.rain*100), 4));
		result = result.concat("}");
		return result;
	}
	
	private String extendString(int value, int length) {
		String result = Integer.toString(value);
		if (result.length() > length) {
			return result.substring(result.length()-length, result.length());
		}
		for (int i = 1; i <= length; i++) {
			if (result.length() < i) {
				result = "0".concat(result);
			}
		}
		return result;
	}
	
	private String sendToBasis(String message) throws BasisException {
		String answer = null;
		try {
			Socket clientSocket = new Socket(this.ip, this.port);
			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			outToServer.writeBytes(message);
			System.out.println("wachten");
			answer = inFromServer.readLine();
			System.out.println(answer);
			clientSocket.close();
		}
		catch (IOException e) {
			e.printStackTrace();
			throw new BasisException("Communication with BASIS failed!");
		}
		return answer;
	}
	
	private String fileStream(int dayIndex) throws BasisException {
		String completeAnswer = "";
		try {
			Socket clientSocket = new Socket(this.ip, this.port);
			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			outToServer.writeBytes("P01 01,".concat(this.extendString(dayIndex, 2)));
			String answer = inFromServer.readLine();
			while (answer != null) {
				System.out.println(answer);
				completeAnswer = completeAnswer.concat(answer).concat("\r\n");
				answer = inFromServer.readLine();
			}
			clientSocket.close();
		}
		catch (IOException e) {
			e.printStackTrace();
			throw new BasisException("Transferring file failed!");
		}
		return completeAnswer;
	}
	
	public int getDayIndex() throws BasisException {
		String answer = this.sendToBasis("P01 00");
		return Integer.parseInt(answer.substring(4, 6));
	}
	
	public void transferFiles(int dayIndex) throws BasisException {
		String answer = this.fileStream(dayIndex);
		if (!answer.substring(0, 3).equals("B01")) {
			throw new BasisException("Transferring file failed, wrong answer: ".concat(answer.substring(0, 3)));
		}
		int currentDayIndex = Integer.parseInt(answer.substring(4,6));
		int daysLast = currentDayIndex - dayIndex;
		if (daysLast < 0) {
			daysLast = daysLast + this.maxDays;
		}
		answer = answer.substring(7);
		try {
			while (!answer.substring(0, 9).equals("ENDENDEND")) {
				int nameLength = Integer.parseInt(answer.substring(0,2));
				String fileName = answer.substring(2,nameLength+2);
				int fileLength = Integer.parseInt(answer.substring(2+nameLength,12+nameLength));
				this.handleFile(answer.substring(12+nameLength,12+nameLength+fileLength), fileName, daysLast);
				answer = answer.substring(12+nameLength+fileLength);
			}
			this.errorCounter = 0;
		}
		catch (NumberFormatException | IndexOutOfBoundsException e) {
			this.errorCounter ++;
			if (this.errorCounter > this.maxErrors) {
				System.out.println("Too much errors occured, transferring failed.");
				this.errorCounter = 0;
				return;
			}
			System.out.println("Error occured, rerun...");
			this.transferFiles(dayIndex);
		}
	}
	
	private void handleFile(String file, String fileName, int daysLast) throws BasisException {
		String[] possibleTypes = { "BASIS", "SENSOR1", "SENSOR2" };
		String type = null;
		for (String ptype : possibleTypes) {
			if (fileName.substring(0,ptype.length()).equals(ptype)) {
				type = ptype;
				break;
			}
		}
		if (type == null) {
			throw new BasisException("Unknown file type: ".concat(fileName));
		}
		DataBaseManager dbManager = new DataBaseManager(type);
		Parser parser = dbManager.addFolder(daysLast);
		parser.writeToFile(file);
	}
	
	public BasisLiveData getLiveData() throws BasisException {
		if (this.liveData == null) {
			this.liveData = this.getLiveDataNow();
			return this.liveData;
		}
		Calendar now = Calendar.getInstance();
		long diff = now.getTime().getTime() - this.liveData.time.getTime().getTime();
		if (diff > this.liveDataMinutes*60*1000) {
			this.liveData = this.getLiveDataNow();
			return this.liveData;
		}
		return this.liveData;
	}
	
	private BasisLiveData getLiveDataNow() throws BasisException {
		String answer = this.sendToBasis("P03");
		String type = answer.substring(0,3);
		if (!type.equals("B03")) {
			throw new BasisException("Communication with BASIS failed! Wrong type.");
		}
		String[] parts = answer.substring(4).split("#");
		if (parts.length != 11) {
			throw new BasisException("Communication with BASIS failed! Wrong length.");
		}
		Calendar time = Calendar.getInstance();
		return new BasisLiveData(Float.parseFloat(parts[0]), Float.parseFloat(parts[1]), Float.parseFloat(parts[2]), Float.parseFloat(parts[3]), Float.parseFloat(parts[4]), Float.parseFloat(parts[5]), Float.parseFloat(parts[6]), Integer.parseInt(parts[7]), Integer.parseInt(parts[8]), Float.parseFloat(parts[9]), Float.parseFloat(parts[10]), time);
	}
}
