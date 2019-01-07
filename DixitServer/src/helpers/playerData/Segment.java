package helpers.playerData;

import java.util.ArrayList;

public class Segment {
	
	public Segment(ArrayList<Integer> x, ArrayList<Integer> y, String color, int size) {
		this.x = x;
		this.y = y;
		this.color = color;
		this.size = size;
	}
	
	public final ArrayList<Integer> x;
	
	public final ArrayList<Integer> y;
	
	public final String color;
	
	public final int size;
}
