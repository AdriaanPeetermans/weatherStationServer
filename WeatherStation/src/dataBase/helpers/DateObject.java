package dataBase.helpers;

public class DateObject {
	
	public DateObject(int day, int month, int year, String type) {
		this.day = day;
		this.month = month;
		this.year = year;
		this.type = type;
	}
	
	public String toString() {
		return String.format("%02d", this.day).concat("/").concat(String.format("%02d", this.month)).concat("/").concat(String.format("%02d", this.year)).concat("/").concat(this.type);
	}
	
	public boolean equals(Object other) {
		if (other.getClass() != this.getClass()) {
			return false;
		}
		DateObject otherDate = (DateObject) other;
		return (otherDate.type.equals(this.type)) && (otherDate.year == this.year) && (otherDate.month == this.month) && (otherDate.day == this.day);
	}
	
	public int hashCode() {
		return this.type.hashCode() ^ this.day ^ this.month ^ this.year;
	}
	
	public final int day;
	
	public final int month;
	
	public final int year;
	
	public final String type;
}
