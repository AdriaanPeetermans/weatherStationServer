package dataBase;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;

import dataBase.helpers.DateObject;

public class HardDriveParser {
	
	private final String filePath = "src/dataBase/Settings/HardDrive.txt";
	
	public HashSet<DateObject> getDates() {
		HashSet<String> lines = this.getLines();
		HashSet<DateObject> result = new HashSet<DateObject>(lines.size());
		for (String line : lines) {
			String[] parts = line.split("/");
			int day = Integer.parseInt(parts[0]);
			int month = Integer.parseInt(parts[1]);
			int year = Integer.parseInt(parts[2]);
			String type = parts[3];
			result.add(new DateObject(day, month, year, type));
		}
		return result;
	}
	
	private HashSet<String> getLines() {
		HashSet<String> result = new HashSet<String>(10);
		try {
			File file = new File(this.filePath);
			if (!file.exists()) {
				return null;
			}
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
	
	private void appendLine(String line) {
		try (FileWriter fw = new FileWriter(this.filePath, true);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter out = new PrintWriter(bw)) {
			out.print(line.concat("\r\n"));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void appendDate(DateObject date) {
		HashSet<DateObject> dates = this.getDates();
		if (dates.contains(date)) {
			return;
		}
		this.appendLine(date.toString());
	}
	
	private void removeDates() {
		PrintWriter writer;
		try {
			writer = new PrintWriter(new File(this.filePath));
			writer.print("");
			writer.close();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void removeDate(DateObject date) {
		HashSet<DateObject> dates = this.getDates();
		if (!dates.contains(date)) {
			return;
		}
		dates.remove(date);
		this.removeDates();
		for (DateObject d : dates) {
			this.appendDate(d);
		}
	}
}
