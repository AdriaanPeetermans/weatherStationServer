package basis;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;

import dataBase.DataBaseManager;
import dataBase.Parser;
import exceptions.BasisException;

public class SensorServer {
	
	public SensorServer(String basisIP, int basisPort, int thisPort) throws IOException {
		this.basisIP = basisIP;
		this.basisPort = basisPort;
		this.welcomeSocket = new ServerSocket(thisPort);
		this.bh = new BasisHandler(this.basisIP, this.basisPort, 10, 5, 5);
	}
	
	private final String basisIP;
	
	private final int basisPort;
	
	private final ServerSocket welcomeSocket;
	
	private final BasisHandler bh;
	
	private int sensor1RefreshMinutes = 5;
	
	private int sensor2RefreshMinutes = 5;
	
	public void run() throws IOException, BasisException {
		String clientMessage;
		String clientAnswer;
		while (true) {
			Socket connectionSocket = welcomeSocket.accept();
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
			clientMessage = inFromClient.readLine();
			clientAnswer = this.handleMessage(clientMessage);
			outToClient.writeBytes(clientAnswer.concat("\r\n"));
			String refreshMinutes = this.bh.sendToBasis(clientMessage.concat("\r\n"));
			switch (clientMessage.charAt(0)) {
				case '1':
					this.sensor1RefreshMinutes = Integer.parseInt(refreshMinutes.substring(4));
					break;
				case '2':
					this.sensor2RefreshMinutes = Integer.parseInt(refreshMinutes.substring(4));
					break;
			}
		}
	}
	
	private String handleMessage(String message) {
		char device = message.charAt(0);
		char type1 = message.charAt(1);
		char type2 = message.charAt(2);
		message = message.substring(4);
		switch (device) {
			case '1':
				switch (type1) {
					case '0':
						switch (type2) {
							case '0':
								this.handleSensor1(message);
								return "B00 ".concat(Integer.toString(this.sensor1RefreshMinutes));
						}
						break;
				}
				break;
			case '2':
				switch (type1) {
					case '0':
						switch (type2) {
							case '0':
								this.handleSensor2(message);
								return "B00 ".concat(Integer.toString(this.sensor2RefreshMinutes));
						}
						break;
				}
				break;
		}
		return "ERROR";
	}
	
	private void handleSensor1(String message) {
		System.out.println(message);
		String[] parts = message.split("#");
		int light = Integer.parseInt(parts[0]);
		float temp1 = Float.parseFloat(parts[1]);
		float hum = Float.parseFloat(parts[2]);
		float temp2 = Float.parseFloat(parts[3]);
		float press = Float.parseFloat(parts[4]);
		float PV = Float.parseFloat(parts[5]);
		float BV = Float.parseFloat(parts[6]);
		DataBaseManager dbManager = new DataBaseManager("SENSOR1");
		Parser parser = dbManager.addFolder(0);
		Calendar cal = Calendar.getInstance();
		parser.appendFile(Integer.toString(light).concat(",").concat(Float.toString(hum)).concat(",").concat(Float.toString((temp1+temp2)/2)).concat(",").concat(Float.toString(press)).concat(",").concat(Float.toString(PV)).concat(",").concat(Float.toString(BV)).concat(" ").concat(this.extendString(cal.get(Calendar.HOUR_OF_DAY), 2)).concat(":").concat(this.extendString(cal.get(Calendar.MINUTE), 2)).concat(":").concat(this.extendString(cal.get(Calendar.SECOND), 2)).concat(" ").concat(this.extendString(cal.get(Calendar.DATE), 2)).concat("/").concat(this.extendString(cal.get(Calendar.MONTH)+1, 2)).concat("/").concat(this.extendString(cal.get(Calendar.YEAR)-2000, 2)).concat("\r\n"));
	}
	
	private void handleSensor2(String message) {
		
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
}
