package siteServer;

import java.util.Calendar;

import basis.BasisHandler;
import basis.helpers.BasisLiveData;
import dataBase.Parser;
import dataBase.helpers.BasisData;
import dataBase.helpers.Sensor1Data;
//import exceptions.BasisException;
import siteServer.helpers.SocketMessage;

public class LiveDataServer extends Server {
	
	public LiveDataServer(int port, String ip, int basisPort, int maxDays, int maxErrors, int liveDataMinutes) {
		super(port);
		this.bh = new BasisHandler(ip, basisPort, maxDays, maxErrors, liveDataMinutes);
	}
	
	public final BasisHandler bh;
	
	/**
	 * Server sends back:
	 * 	tempOut#tempSer#tempIn#moistOut#moistSer#moistIn#press#lightOut#lightIn#pv#bv#
	 */
	@Override
	public void handleMessage(SocketMessage t) {
		String answer = "ERROR";
//		try {
			//BasisLiveData data = this.bh.getLiveData();
			//BASIS:
			boolean dayExist = false;
			int lastDay = 0;
			BasisData basisData = null;
			while (!dayExist) {
				Calendar now = Calendar.getInstance();
				now.add(Calendar.DAY_OF_YEAR, -lastDay);
				Parser basisParser = new Parser("BASIS", now.get(Calendar.YEAR), now.get(Calendar.MONTH)+1, now.get(Calendar.DATE), true);
				basisData = (BasisData) basisParser.readFile();
				if (basisData.exist) {
					dayExist = true;
				}
				else {
					lastDay ++;
				}
			}
			//SENSOR1:
			dayExist = false;
			lastDay = 0;
			Sensor1Data sensor1Data = null;
			while (!dayExist) {
				Calendar now = Calendar.getInstance();
				now.add(Calendar.DAY_OF_YEAR, -lastDay);
				Parser sensor1Parser = new Parser("SENSOR1", now.get(Calendar.YEAR), now.get(Calendar.MONTH)+1, now.get(Calendar.DATE), true);
				sensor1Data = (Sensor1Data) sensor1Parser.readFile();
				if (sensor1Data.exist) {
					dayExist = true;
				}
				else {
					lastDay ++;
				}
			}
			int lastBasis = basisData.time.size()-1;
			int lastSensor1 = sensor1Data.time.size()-1;
			Calendar latestDate;
			if (basisData.time.get(lastBasis).before(sensor1Data.time.get(lastSensor1))) {
				latestDate = sensor1Data.time.get(lastSensor1);
			}
			else {
				latestDate = basisData.time.get(lastBasis);
			}
			BasisLiveData data = new BasisLiveData(sensor1Data.temperature.get(lastSensor1), 0, basisData.temperature.get(lastBasis), sensor1Data.moisture.get(lastSensor1), 0, basisData.moisture.get(lastBasis), sensor1Data.pressure.get(lastSensor1), sensor1Data.light.get(lastSensor1), basisData.light.get(lastBasis), sensor1Data.panelVoltage.get(lastSensor1), sensor1Data.batteryVoltage.get(lastSensor1), latestDate);
			answer = Float.toString(data.tempOut).concat("#");
			answer = answer.concat(Float.toString(data.tempSer)).concat("#");
			answer = answer.concat(Float.toString(data.tempIn)).concat("#");
			answer = answer.concat(Float.toString(data.moistOut)).concat("#");
			answer = answer.concat(Float.toString(data.moistSer)).concat("#");
			answer = answer.concat(Float.toString(data.moistIn)).concat("#");
			answer = answer.concat(Float.toString(data.press)).concat("#");
			answer = answer.concat(Float.toString(data.lightOut)).concat("#");
			answer = answer.concat(Float.toString(data.lightIn)).concat("#");
			answer = answer.concat(Float.toString(data.pv)).concat("#");
			answer = answer.concat(Float.toString(data.bv)).concat("#");
			answer = answer.concat(this.extend(data.time.get(Calendar.HOUR_OF_DAY),2)).concat(":").concat(this.extend(data.time.get(Calendar.MINUTE),2)).concat(" - ").concat(this.extend(data.time.get(Calendar.DATE),2)).concat("/").concat(this.extend(data.time.get(Calendar.MONTH)+1,2)).concat("/").concat(this.extend(data.time.get(Calendar.YEAR),4)).concat("#");
//		} catch (BasisException e) {
//			e.printStackTrace();
//		}
		t.sock.send(answer);
	}
	
	private String extend(int num, int length) {
		String result = Integer.toString(num);
		int l = result.length();
		if (l < length) {
			for (int i = 0; i < length-l; i++) {
				result = "0".concat(result);
			}
		}
		else {
			result = result.substring(l-length,l);
		}
		return result;
	}
}
