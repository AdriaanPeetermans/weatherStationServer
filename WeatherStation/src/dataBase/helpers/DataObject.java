package dataBase.helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public abstract class DataObject {
	
	public DataObject(String type, int day, int month, int year) {
		this.type = type;
		this.day = day;
		this.month = month;
		this.year = year;
	}
	
	public final String type;
	
	public final int day;
	
	public final int month;
	
	public final int year;
	
	public boolean exist = false;
	
	public ArrayList<Calendar> time = new ArrayList<Calendar>();
	
	public abstract void parse();
	
	public abstract void fixData();
	
	/**
	 * Adjust the sample frequency of the collected data.
	 * 	Linear combination of closest 2 values is made.
	 * 
	 * @param minutes	The new sample frequency, given in minutes/sample.
	 */
	public abstract void adjustData(int minutes, int startMinute);
	
	protected void fixDataFloat(ArrayList<Float> data, int maxDeviationPerc, float min, float max) {
		if (data.size() <= 1) {
			return;
		}
		ArrayList<Float> result = new ArrayList<Float>(data.size());
		for (int i = 0; i < data.size(); i++) {
			if ((i != 0) && (i != data.size()-1)) {
				if ((data.get(i) > max) || (data.get(i) < min)) {
					result.add((float) ((result.get(i-1)+data.get(i+1))/2.0));
					continue;
				}
				if ((Math.abs(data.get(i-1)) > 0.01) && (Math.abs(data.get(i+1)) > 0.01)) {
					if ((Math.abs((data.get(i) - data.get(i-1))/data.get(i-1)) > maxDeviationPerc/100.0) && (Math.abs((data.get(i) - data.get(i+1))/data.get(i+1)) > maxDeviationPerc/100.0) && (data.get(i+1) > min) && (data.get(i+1) < max)) {
						result.add((float) ((result.get(i-1)+data.get(i+1))/2.0));
					}
					else {
						result.add(data.get(i));
					}
				}
				else {
					float mean = (float) ((result.get(i-1)+data.get(i+1))/2.0);
					if (mean != 0) {
						if ((Math.abs((data.get(i)-mean)/mean) > maxDeviationPerc/50.0) && (data.get(i+1) > min) && (data.get(i+1) < max)) {
							result.add((float) mean);
						}
						else {
							result.add(data.get(i));
						}
					}
					else {
						result.add((float) mean);
					}
				}
			}
			else {
				if (i == 0) {
					if ((data.get(i) > max) || (data.get(i) < min)) {
						result.add(data.get(i+1));
						continue;
					}
					if (data.get(i+1) != 0) {
						if ((Math.abs((data.get(i)-data.get(i+1))/data.get(i+1)) > maxDeviationPerc/100.0) && (data.get(i+1) > min) && (data.get(i+1) < max)) {
							result.add(data.get(i+1));
						}
						else {
							result.add(data.get(i));
						}
					}
					else {
						if (Math.abs(data.get(i)) > maxDeviationPerc/50.0) {
							result.add((float) 0);
						}
						else {
							result.add(data.get(i));
						}
					}
				}
				else {
					if ((data.get(i) > max) || (data.get(i) < min)) {
						result.add(result.get(i-1));
						continue;
					}
					if (result.get(i-1) != 0) {
						if (Math.abs((data.get(i)-result.get(i-1))/result.get(i-1)) > maxDeviationPerc/100.0) {
							result.add(result.get(i-1));
						}
						else {
							result.add(data.get(i));
						}
					}
					else {
						if (Math.abs(data.get(i)) > maxDeviationPerc/50.0) {
							result.add((float) 0);
						}
						else {
							result.add(data.get(i));
						}
					}
				}
			}
		}
		for (int j = data.size()-1; j >= 0; j--) {
			data.remove(j);
		}
		for (int j = 0; j < result.size(); j++) {
			data.add(result.get(j));
		}
	}
	
	protected void fixDataInt(ArrayList<Integer> data, int maxDeviationPerc, int min, int max) {
		if (data.size() <= 1) {
			return;
		}
		ArrayList<Integer> result = new ArrayList<Integer>(data.size());
		for (int i = 0; i < data.size(); i++) {
			if ((i != 0) && (i != data.size()-1)) {
				if ((data.get(i) > max) || (data.get(i) < min)) {
					result.add((int) ((result.get(i-1)+data.get(i+1))/2.0));
					continue;
				}
				if ((Math.abs(result.get(i-1)) > 0.01) && (Math.abs(data.get(i+1)) > 0.01)) {
					if ((Math.abs((float) (data.get(i) - result.get(i-1))/result.get(i-1)) > maxDeviationPerc/100.0) && (Math.abs((float) (data.get(i) - data.get(i+1))/data.get(i+1)) > maxDeviationPerc/100.0) && (data.get(i+1) > min) && (data.get(i+1) < max)) {
						result.add((int) ((result.get(i-1)+data.get(i+1))/2.0));
					}
					else {
						result.add(data.get(i));
					}
				}
				else {
					float mean = (float) ((result.get(i-1)+data.get(i+1))/2.0);
					if (mean != 0) {
						if ((Math.abs((data.get(i)-mean)/mean) > maxDeviationPerc/50.0) && (data.get(i+1) > min) && (data.get(i+1) < max)) {
							result.add((int) mean);
						}
						else {
							result.add(data.get(i));
						}
					}
					else {
						result.add((int) mean);
					}
				}
			}
			else {
				if (i == 0) {
					if ((data.get(i) > max) || (data.get(i) < min)) {
						result.add(data.get(i+1));
						continue;
					}
					if (data.get(i+1) != 0) {
						if ((Math.abs((float) (data.get(i)-data.get(i+1))/data.get(i+1)) > maxDeviationPerc/100.0) && (data.get(i+1) > min) && (data.get(i+1) < max)) {
							result.add(data.get(i+1));
						}
						else {
							result.add(data.get(i));
						}
					}
					else {
						if (Math.abs(data.get(i)) > maxDeviationPerc/50.0) {
							result.add((int) 0);
						}
						else {
							result.add(data.get(i));
						}
					}
				}
				else {
					if ((data.get(i) > max) || (data.get(i) < min)) {
						result.add(result.get(i-1));
						continue;
					}
					if (result.get(i-1) != 0) {
						if (Math.abs((float) (data.get(i)-result.get(i-1))/result.get(i-1)) > maxDeviationPerc/100.0) {
							result.add(result.get(i-1));
						}
						else {
							result.add(data.get(i));
						}
					}
					else {
						if (Math.abs(data.get(i)) > maxDeviationPerc/50.0) {
							result.add((int) 0);
						}
						else {
							result.add(data.get(i));
						}
					}
				}
			}
		}
		for (int j = data.size()-1; j >= 0; j--) {
			data.remove(j);
		}
		for (int j = 0; j < result.size(); j++) {
			data.add(result.get(j));
		}
	}
	
	protected void adjustFloat(ArrayList<Float> data, int minutes, int startMinute) {
		int nbFirstOne = 1;
		if (data.size() > 1) {
			int min0 = this.time.get(0).get(Calendar.MINUTE) + this.time.get(0).get(Calendar.HOUR_OF_DAY)*60;
			int min1 = this.time.get(1).get(Calendar.MINUTE) + this.time.get(1).get(Calendar.HOUR_OF_DAY)*60;
			nbFirstOne = (int) ((min1-min0) / minutes);
		}
		int nbLastOne = nbFirstOne;
		int nbSamples = (int) (24.0*60/minutes);
		if (!this.exist) {
			for (int i = 0; i < nbSamples; i++) {
				data.add(null);
			}
			return;
		}
		ArrayList<Float> result = new ArrayList<Float>(nbSamples);
		int currentMin = startMinute;
		int i = 0;
		while (currentMin < 24*60) {
			//System.out.println(currentMin);
			int minI = 24*60;
			if (i < data.size()) {
				minI = this.time.get(i).get(Calendar.MINUTE) + this.time.get(i).get(Calendar.HOUR_OF_DAY)*60;
			}
			//System.out.println(minI);
			if (minI >= currentMin) {
				if (i != 0) {
					int prevMin = this.time.get(i-1).get(Calendar.MINUTE) + this.time.get(i-1).get(Calendar.HOUR_OF_DAY)*60;
					if (i < data.size()) {
						//System.out.println("gewone");
						result.add(this.interpolate(prevMin, minI, data.get(i-1), data.get(i), currentMin));
					}
					else {
						//System.out.println("einde");
						//result.add(this.interpolate(this.time.get(data.size()-2).get(Calendar.MINUTE)+this.time.get(data.size()-2).get(Calendar.HOUR_OF_DAY)*60, this.time.get(data.size()-1).get(Calendar.MINUTE)+this.time.get(data.size()-1).get(Calendar.HOUR_OF_DAY)*60, data.get(data.size()-2), data.get(data.size()-1), currentMin));
						if ((nbLastOne > 0) && (data.size() > 1)) {
							result.add(this.interpolate(this.time.get(data.size()-2).get(Calendar.MINUTE)+this.time.get(data.size()-2).get(Calendar.HOUR_OF_DAY)*60, this.time.get(data.size()-1).get(Calendar.MINUTE)+this.time.get(data.size()-1).get(Calendar.HOUR_OF_DAY)*60, data.get(data.size()-2), data.get(data.size()-1), currentMin));
							nbLastOne --;
						}
						else {
							result.add(null);
						}
					}
				}
				else {
					//System.out.println("begin");
					if ((currentMin + minutes > minI) && (data.size() > 1)) {
						result.add(this.interpolate(this.time.get(0).get(Calendar.MINUTE)+this.time.get(0).get(Calendar.HOUR_OF_DAY)*60, this.time.get(1).get(Calendar.MINUTE)+this.time.get(1).get(Calendar.HOUR_OF_DAY)*60, data.get(0), data.get(1), currentMin));
					}
					else {
						if (nbFirstOne > 0) {
							result.add(data.get(0));
							nbFirstOne --;
						}
						else {
							result.add(null);
						}
					}
				}
				currentMin = currentMin + minutes;
			}
			if (minI < currentMin) {
				i++;
			}
		}
		for (int j = data.size()-1; j >= 0; j--) {
			data.remove(j);
		}
		for (int j = 0; j < result.size(); j++) {
			data.add(result.get(j));
		}
	}
	
	protected void adjustInt(ArrayList<Integer> data, int minutes, int startMinute) {
		int nbFirstOne = 1;
		if (data.size() > 1) {
			int min0 = this.time.get(0).get(Calendar.MINUTE) + this.time.get(0).get(Calendar.HOUR_OF_DAY)*60;
			int min1 = this.time.get(1).get(Calendar.MINUTE) + this.time.get(1).get(Calendar.HOUR_OF_DAY)*60;
			nbFirstOne = (int) ((min1-min0) / minutes);
		}
		int nbLastOne = nbFirstOne;
		int nbSamples = (int) (24.0*60/minutes);
		if (!this.exist) {
			for (int i = 0; i < nbSamples; i++) {
				data.add(null);
			}
			return;
		}
		ArrayList<Integer> result = new ArrayList<Integer>(nbSamples);
		int currentMin = startMinute;
		int i = 0;
		while (currentMin < 24*60) {
			//System.out.println(currentMin);
			int minI = 24*60;
			if (i < data.size()) {
				minI = this.time.get(i).get(Calendar.MINUTE) + this.time.get(i).get(Calendar.HOUR_OF_DAY)*60;
			}
			//System.out.println(minI);
			if (minI >= currentMin) {
				if (i != 0) {
					int prevMin = this.time.get(i-1).get(Calendar.MINUTE) + this.time.get(i-1).get(Calendar.HOUR_OF_DAY)*60;
					if (i < data.size()) {
						//System.out.println("gewone");
						result.add((int) this.interpolate(prevMin, minI, data.get(i-1), data.get(i), currentMin));
					}
					else {
						//System.out.println("einde");
						if ((nbLastOne > 0) && (data.size() > 1)) {
							result.add((int) this.interpolate(this.time.get(data.size()-2).get(Calendar.MINUTE)+this.time.get(data.size()-2).get(Calendar.HOUR_OF_DAY)*60, this.time.get(data.size()-1).get(Calendar.MINUTE)+this.time.get(data.size()-1).get(Calendar.HOUR_OF_DAY)*60, data.get(data.size()-2), data.get(data.size()-1), currentMin));
							nbLastOne --;
						}
						else {
							result.add(null);
						}
					}
				}
				else {
					//System.out.println("begin");
					if ((currentMin + minutes > minI) && (data.size() > 1)) {
						result.add((int) this.interpolate(this.time.get(0).get(Calendar.MINUTE)+this.time.get(0).get(Calendar.HOUR_OF_DAY)*60, this.time.get(1).get(Calendar.MINUTE)+this.time.get(1).get(Calendar.HOUR_OF_DAY)*60, data.get(0), data.get(1), currentMin));
					}
					else {
						if (nbFirstOne > 0) {
							result.add(data.get(0));
							nbFirstOne --;
						}
						else {
							result.add(null);
						}
					}
				}
				currentMin = currentMin + minutes;
			}
			if (minI < currentMin) {
				i++;
			}
		}
		for (int j = data.size()-1; j >= 0; j--) {
			data.remove(j);
		}
		for (int j = 0; j < result.size(); j++) {
			data.add(result.get(j));
		}
	}
	
	private float interpolate(int m0, int m1, float val0, float val1, int m2) {
		if (m0 == m1) {
			return (float) ((val0+val1)/2.0);
		}
		return val0+(val1-val0)/(m1-m0)*(m2-m0);
	}
	
	protected void adjustTime(int minutes, int startMinute) {
		int nbSamples = (int) (24.0*60/minutes);
		ArrayList<Calendar> result = new ArrayList<Calendar>(nbSamples);
		int currentMin = startMinute;
		int currentHour = 0;
		Calendar day;
		if (this.exist) {
			day = (Calendar) this.time.get(0).clone();
		}
		else {
			day = Calendar.getInstance();
			day.set(Calendar.DATE, this.day);
			day.set(Calendar.MONTH, this.month-1);
			day.set(Calendar.YEAR, this.year);
		}
		day.set(Calendar.HOUR_OF_DAY, 0);
		day.set(Calendar.MINUTE, 0);
		day.set(Calendar.SECOND, 0);
		while (currentMin+currentHour*60 < 24*60) {
			Calendar current = (Calendar) day.clone();
			current.set(Calendar.HOUR_OF_DAY, currentHour);
			current.set(Calendar.MINUTE, currentMin);
			result.add(current);
			currentMin = currentMin + minutes;
			while (currentMin >= 60) {
				currentMin = currentMin - 60;
				currentHour = currentHour + 1;
			}
		}
		this.time = result;
	}
	
	public float getMeanFloat(ArrayList<Float> data) {
		float result = 0;
		for (int i = 0; i < data.size(); i++) {
			result = result + data.get(i);
		}
		return result / data.size();
	}
	
	public int getMeanInt(ArrayList<Integer> data) {
		float result = 0;
		for (int i = 0; i < data.size(); i++) {
			result = result + data.get(i);
		}
		return (int) (result / data.size());
	}
	
	public float getMaxFloat(ArrayList<Float> data) {
		if (data.size() == 0) {
			return -1;
		}
		float max = data.get(0);
		for (float d : data) {
			if (d > max) {
				max = d;
			}
		}
		return max;
	}
	
	public float getMinFloat(ArrayList<Float> data) {
		if (data.size() == 0) {
			return -1;
		}
		float min = data.get(0);
		for (float d : data) {
			if (d < min) {
				min = d;
			}
		}
		return min;
	}
	
	public ArrayList<String> getLines() {
		ArrayList<String> result = new ArrayList<String>(242);
		try {
			File file = new File("src/dataBase/".concat(this.type).concat("/").concat(Integer.toString(this.year).concat("/").concat(this.extendString(this.month, 2).concat("/")).concat(this.extendString(this.day, 2)).concat(".txt")));
			if (!file.exists()) {
				return null;
			}
			this.exist = true;
			BufferedReader br = new BufferedReader(new FileReader(file));
			String st = br.readLine();
			while (st != null) {
				result.add(st);
				st = br.readLine();
			}
			br.close();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public String getSize() {
		File file = new File("src/dataBase/".concat(this.type).concat("/").concat(Integer.toString(this.year).concat("/").concat(this.extendString(this.month, 2).concat("/")).concat(this.extendString(this.day, 2)).concat(".txt")));
		if (!file.exists()) {
			return null;
		}
		this.exist = true;
		return Double.toString(((double) file.length())/1000);
	}
	
	protected String extendString(int value, int length) {
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
