package helpers.playerData;

import java.util.ArrayList;

public class Segment {
	
	public Segment(ArrayList<Integer> x, ArrayList<Integer> y, String color, float size) {
		this.x = x;
		this.y = y;
		this.color = color;
		this.size = size;
	}
	
	public final ArrayList<Integer> x;
	
	public final ArrayList<Integer> y;
	
	public final String color;
	
	public final float size;
	
	public String toString() {
		String result = this.color.concat("&");
		result = result.concat(Float.toString(this.size)).concat("&");
		result = result.concat(Integer.toString(this.x.size())).concat("&");
		for (int i = 0; i < this.x.size(); i++) {
			result = result.concat(Integer.toString(this.x.get(i))).concat("&");
			result = result.concat(Integer.toString(this.y.get(i))).concat("&");
		}
		return result;
	}
}
