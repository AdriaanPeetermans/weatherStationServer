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
	
	public static ArrayList<Segment> parseSegment(String drawing) {
		String[] segments = drawing.split("%");
		ArrayList<Segment> segs = new ArrayList<Segment>(segments.length);
		for (String seg : segments) {
			String[] segParts = seg.split("&");
			String color = segParts[0];
			float size = Float.parseFloat(segParts[1]);
			int segLength = Integer.parseInt(segParts[2]);
			ArrayList<Integer> segx = new ArrayList<Integer>(segLength);
			ArrayList<Integer> segy = new ArrayList<Integer>(segLength);
			for (int i = 0; i < segLength; i++) {
				segx.add(Integer.parseInt(segParts[3+2*i]));
				segy.add(Integer.parseInt(segParts[4+2*i]));
			}
			Segment segment = new Segment(segx, segy, color, size);
			segs.add(segment);
		}
		return segs;
	}
}
