package siteServer;

import java.util.Calendar;

import basis.BasisHandler;
import basis.helpers.BasisLiveData;
import exceptions.BasisException;
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
		try {
			BasisLiveData data = this.bh.getLiveData();
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
		} catch (BasisException e) {
			e.printStackTrace();
		}
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
