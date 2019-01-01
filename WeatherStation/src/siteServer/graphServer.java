package siteServer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import dataBase.Parser;
import dataBase.helpers.BasisData;
import dataBase.helpers.Sensor1Data;
import siteServer.Server;
import siteServer.helpers.SocketMessage;

public class graphServer extends Server {
	
	public graphServer(int port) {
		super(port);
	}
	
	private final List<String> weekdaysNL = Arrays.asList("Zondag", "Maandag", "Dinsdag", "Woensdag", "Donderdag", "Vrijdag", "Zaterdag");
	
	private final String weekNL = "Week";
	
	private final List<String> monthsNL = Arrays.asList("Januari", "Februari", "Maart", "April", "Mei", "Juni", "Juli", "Augustus", "September", "Oktober", "November", "December");
	
	private final String allTimeNL = "Sinds het begin";
	
	/**
	 * Possible messages:	(%..% means variable)
	 * 		%GraphType% + "#" + %PeriodLength% + "#" + %WhichPeriod% + "#" + %Minutes% + "#" + %startMinute%
	 * 			GraphType:
	 * 				0:	Temp+Moist+Press+Light+PV+BV
	 * 				1:	MaxTemp+MinTemp+MeanTemp
	 * 				2:	TimeSunUp+TimeSunDown+SunTime
	 * 				3:	LightIntensity
	 * 			PeriodLength:
	 * 				0:	Day
	 * 				1:	Week
	 * 				2:	Month
	 * 				3: 	Year
	 * 				4: 	All time
	 * 				For Year and All time, the mean day temperature is given.
	 * 			WhichPeriod:
	 * 				Number of periods it has been. 0 includes today, 1 is yesterday, last week last month, ...
	 * 			Minutes:
	 * 				Sample period in minutes (discarded for year and all time)
	 * 			Server sends back:
	 * 				%periodName% + "#" + %periodTimeFirstDay% + "#" + %numberGraphs% + "#" + %name1% + "#" + %numberPoints1% + "#" + temp0,moist0,light0,press0,pv0,bv0 ... ;subperiod10X,subperiod10Y; ... #
	 * Everything else will be answered by the message: "ERROR"
	 */
	public void handleMessage(SocketMessage t) {
		String[] mes = t.message.split("#");
		if (mes.length != 5) {
			t.sock.send("ERROR");
			return;
		}
		System.out.println(t.message);
		int graphType = Integer.parseInt(mes[0]);
		int periodLength = Integer.parseInt(mes[1]);
		int whichPeriod = Integer.parseInt(mes[2]);
		if ((graphType < 0) || (graphType > 3) || (periodLength < 0) || (periodLength > 4) || (whichPeriod < 0)) {
			t.sock.send("ERROR");
			return;
		}
		int minutes = Integer.parseInt(mes[3]);
		if (minutes <= 0) {
			t.sock.send("ERROR");
			return;
		}
		int startMinute = Integer.parseInt(mes[4]);
		if (startMinute <= 0) {
			t.sock.send("ERROR");
			return;
		}
		t.sock.send(this.createGraph(graphType, periodLength, whichPeriod, minutes, startMinute));
	}
	
	public String createGraph(int type, int period, int which, int minutes, int startMinute) {
		ArrayList<Calendar> days = this.giveDays(period, which);
		String result = this.getPeriodName(period, which).concat("#");
		result = result.concat(this.getPeriodTime(days.get(0))).concat("#");
		switch (type) {
			case 0:		//Temp+Moist+Press+Light+PV+BV
				if ((period != 3) && (period != 4)) {
					String basisPoints = "";
					String sensor1Points = "";
					int nbBasisPoints = 0;
					int nbSensor1Points = 0;
					for (int i = 0; i < days.size(); i++) {
						Parser basisParser = new Parser("BASIS", days.get(i).get(Calendar.YEAR), days.get(i).get(Calendar.MONTH)+1, days.get(i).get(Calendar.DATE), true);
						Parser sensor1Parser = new Parser("SENSOR1", days.get(i).get(Calendar.YEAR), days.get(i).get(Calendar.MONTH)+1, days.get(i).get(Calendar.DATE), true);
						BasisData basisData = (BasisData) basisParser.readFile();
						Sensor1Data sensor1Data = (Sensor1Data) sensor1Parser.readFile();
						basisData.adjustData(minutes, startMinute);
						
						sensor1Data.adjustData(minutes, startMinute);
						
						nbBasisPoints = nbBasisPoints + basisData.time.size();
						for (int j = 0; j < basisData.time.size(); j++) {
							//float pointX = this.getX(days.get(0), basisData.time.get(j), period);
							//long pointX = this.getXUNIX(basisData.time.get(j));
							basisPoints = basisPoints.concat(Float.toString(basisData.temperature.get(j))).concat(",").concat(Float.toString(basisData.moisture.get(j))).concat(",").concat(Integer.toString(basisData.light.get(j))).concat(",");
						}
						nbSensor1Points = nbSensor1Points + sensor1Data.time.size();
						for (int j = 0; j < sensor1Data.time.size(); j++) {
							//float pointX = this.getX(days.get(0), sensor1Data.time.get(j), period);
							//long pointX = this.getXUNIX(sensor1Data.time.get(j));
							sensor1Points = sensor1Points.concat(Float.toString(sensor1Data.temperature.get(j))).concat(",").concat(Float.toString(sensor1Data.moisture.get(j))).concat(",").concat(Integer.toString(sensor1Data.light.get(j))).concat(",").concat(Float.toString(sensor1Data.pressure.get(j))).concat(",").concat(Float.toString(sensor1Data.panelVoltage.get(j))).concat(",").concat(Float.toString(sensor1Data.batteryVoltage.get(j))).concat(",");
						}
					}
					result = result.concat("2#BASIS#");
					result = result.concat(Integer.toString(nbBasisPoints)).concat("#").concat(basisPoints).concat("#SENSOR1#").concat(Integer.toString(nbSensor1Points)).concat("#").concat(sensor1Points).concat("#");
				}
				else {
					String basisPoints = "";
					String sensor1Points = "";
					int nbBasisPoints = 0;
					int nbSensor1Points = 0;
					for (int i = 0; i < days.size(); i++) {
						Parser basisParser = new Parser("BASIS", days.get(i).get(Calendar.YEAR), days.get(i).get(Calendar.MONTH)+1, days.get(i).get(Calendar.DATE), true);
						Parser sensor1Parser = new Parser("SENSOR1", days.get(i).get(Calendar.YEAR), days.get(i).get(Calendar.MONTH)+1, days.get(i).get(Calendar.DATE), true);
						BasisData basisData = (BasisData) basisParser.readFile();
						Sensor1Data sensor1Data = (Sensor1Data) sensor1Parser.readFile();
						
						basisData.fixData();
						sensor1Data.fixData();
						
						nbBasisPoints = nbBasisPoints + 1;
						if (basisData.time.size() != 0) {
							basisPoints = basisPoints.concat(Float.toString(basisData.getMeanFloat(basisData.temperature))).concat(",").concat(Float.toString(basisData.getMeanFloat(basisData.moisture))).concat(",").concat(Integer.toString(basisData.getMeanInt(basisData.light))).concat(",");
						}
						else {
							basisPoints = basisPoints.concat("0,0,0,");
						}
						nbSensor1Points = nbSensor1Points + 1;
						if (sensor1Data.time.size() != 0) {
							sensor1Points = sensor1Points.concat(Float.toString(sensor1Data.getMeanFloat(sensor1Data.temperature))).concat(",").concat(Float.toString(sensor1Data.getMeanFloat(sensor1Data.moisture))).concat(",").concat(Integer.toString(sensor1Data.getMeanInt(sensor1Data.light))).concat(",").concat(Float.toString(sensor1Data.getMeanFloat(sensor1Data.pressure))).concat(",").concat(Float.toString(sensor1Data.getMeanFloat(sensor1Data.panelVoltage))).concat(",").concat(Float.toString(sensor1Data.getMeanFloat(sensor1Data.batteryVoltage))).concat(",");
						}
						else {
							sensor1Points = sensor1Points.concat("0,0,0,0,0,0,");
						}
					}
					result = result.concat("2#BASIS#");
					result = result.concat(Integer.toString(nbBasisPoints)).concat("#").concat(basisPoints).concat("#SENSOR1#").concat(Integer.toString(nbSensor1Points)).concat("#").concat(sensor1Points).concat("#");
				}
				break;
			case 1:		//MaxTemp+MinTemp+MeanTemp
				
				break;
		}
		return result;
	}
	
	private String getPeriodTime(Calendar day) { //"2013-05-09T00:00:00Z"
		return this.extendString(day.get(Calendar.YEAR),4).concat("-").concat(this.extendString(day.get(Calendar.MONTH)+1,2)).concat("-").concat(this.extendString(day.get(Calendar.DATE), 2));
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
	
	private long getXUNIX(Calendar current) {
		return current.getTime().getTime();
	}
	
	private float getX(Calendar start, Calendar current, int period) {
		start.set(Calendar.SECOND, 0);
		start.set(Calendar.MINUTE, 0);
		start.set(Calendar.HOUR, 0);
		float x = 0;
		switch (period) {
			case 0:
				x = (float) ((current.get(Calendar.HOUR_OF_DAY) + current.get(Calendar.MINUTE)/60.0 + current.get(Calendar.SECOND)/60.0/60.0)/24.0);
				break;
			case 1:
				long diff = current.getTime().getTime() - start.getTime().getTime();
				int days = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
				x = (float) ((days + current.get(Calendar.HOUR_OF_DAY)/24.0 + current.get(Calendar.MINUTE)/60.0/24.0 + current.get(Calendar.SECOND)/60.0/60.0/24.0)/7.0);
				break;
			case 2:
				diff = current.getTime().getTime() - start.getTime().getTime();
				days = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
				x = (float) ((days + current.get(Calendar.HOUR_OF_DAY)/24.0 + current.get(Calendar.MINUTE)/60.0/24.0 + current.get(Calendar.SECOND)/60.0/60.0/24.0)/current.getActualMaximum(Calendar.DATE));
				break;
			case 3:
				diff = current.getTime().getTime() - start.getTime().getTime();
				days = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
				x = (float) ((float) days/current.getActualMaximum(Calendar.DAY_OF_YEAR));
				break;
			case 4:
				Calendar today = Calendar.getInstance();
				long diffT = today.getTime().getTime() - start.getTime().getTime();
				int daysT = (int) TimeUnit.DAYS.convert(diffT, TimeUnit.MILLISECONDS);
				diff = current.getTime().getTime() - start.getTime().getTime();
				days = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
				x = (float) ((float) days/daysT);
				break;
		}
		return x;
	}
	
	private String getPeriodName(int period, int which) {
		String result = "";
		Calendar today = Calendar.getInstance();
		switch (period) {
			case 0:
				today.add(Calendar.DATE, -which);
				result = result.concat(this.weekdaysNL.get(today.get(Calendar.DAY_OF_WEEK)-1));
				result = result.concat(" ").concat(Integer.toString(today.get(Calendar.DAY_OF_MONTH)));
				result = result.concat("/").concat(Integer.toString(today.get(Calendar.MONTH)+1));
				result = result.concat("/").concat(Integer.toString(today.get(Calendar.YEAR)));
				break;
			case 1:
				today.add(Calendar.DATE, -today.get(Calendar.DAY_OF_WEEK)+2-7*which);
				result = result.concat(this.weekNL).concat(": ");
				result = result.concat(Integer.toString(today.get(Calendar.DAY_OF_MONTH)));
				result = result.concat("/").concat(Integer.toString(today.get(Calendar.MONTH)+1));
				result = result.concat("/").concat(Integer.toString(today.get(Calendar.YEAR)));
				result = result.concat(" - ");
				today.add(Calendar.DATE, 6);
				result = result.concat(Integer.toString(today.get(Calendar.DAY_OF_MONTH)));
				result = result.concat("/").concat(Integer.toString(today.get(Calendar.MONTH)+1));
				result = result.concat("/").concat(Integer.toString(today.get(Calendar.YEAR)));
				break;
			case 2:
				today.add(Calendar.DATE, -today.get(Calendar.DAY_OF_MONTH)+1);
				today.add(Calendar.MONTH,-which);
				result = result.concat(this.monthsNL.get(today.get(Calendar.MONTH)));
				result = result.concat(" ").concat(Integer.toString(today.get(Calendar.YEAR)));
				break;
			case 3:
				today.add(Calendar.DATE, -today.get(Calendar.DAY_OF_YEAR)+1);
				today.add(Calendar.YEAR, -which);
				result = result.concat(Integer.toString(today.get(Calendar.YEAR)));
				break;
			case 4:
				result = this.allTimeNL;
		}
		return result;
	}
	
	private ArrayList<Calendar> giveDays(int period, int which) {
		Calendar today = Calendar.getInstance();
		ArrayList<Calendar> result = new ArrayList<Calendar>();
		switch (period) {
			case 0:
				today.add(Calendar.DATE, -which);
				result.add(today);
				break;
			case 1:
				today.add(Calendar.WEEK_OF_YEAR, -which);
				Calendar firstDay = (Calendar) today.clone();
				firstDay.add(Calendar.DATE, -today.get(Calendar.DAY_OF_WEEK)+2);
				result.add(firstDay);
				Calendar prevDay = (Calendar) firstDay.clone();
				for (int i = 0; i < 6; i++) {
					Calendar day = (Calendar) prevDay.clone();
					day.add(Calendar.DATE, 1);
					result.add(day);
					prevDay = day;
				}
				break;
			case 2:
				today.add(Calendar.MONTH, -which);
				firstDay = (Calendar) today.clone();
				firstDay.add(Calendar.DATE, -today.get(Calendar.DATE)+1);
				result.add(firstDay);
				prevDay = (Calendar) firstDay.clone();
				for (int i = 0; i < today.getActualMaximum(Calendar.DATE)-1; i++) {
					Calendar day = (Calendar) prevDay.clone();
					day.add(Calendar.DATE, 1);
					result.add(day);
					prevDay = day;
				}
				break;
			case 3:
				today.add(Calendar.YEAR, -which);
				firstDay = (Calendar) today.clone();
				firstDay.add(Calendar.DATE, -today.get(Calendar.DAY_OF_YEAR)+1);
				result.add(firstDay);
				prevDay = (Calendar) firstDay.clone();
				for (int i = 0; i < today.getActualMaximum(Calendar.DAY_OF_YEAR)-1; i++) {
					Calendar day = (Calendar) prevDay.clone();
					day.add(Calendar.DATE, 1);
					result.add(day);
					prevDay = day;
				}
				break;
			case 4:
				firstDay = (Calendar) today.clone();
				firstDay.set(Calendar.DATE, 12);
				firstDay.set(Calendar.MONTH, 10);
				firstDay.set(Calendar.YEAR, 2018);
				prevDay = (Calendar) firstDay.clone();
				while ((prevDay.get(Calendar.DATE) != today.get(Calendar.DATE)) || (prevDay.get(Calendar.MONTH) != today.get(Calendar.MONTH)) || (prevDay.get(Calendar.YEAR) != today.get(Calendar.YEAR))) {
					result.add(prevDay);
					Calendar day = (Calendar) prevDay.clone();
					day.add(Calendar.DATE, 1);
					prevDay = day;
				}
				result.add(prevDay);
				break;
		}
		return result;
	}
}
