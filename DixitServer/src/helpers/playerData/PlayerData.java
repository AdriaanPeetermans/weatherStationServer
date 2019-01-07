package helpers.playerData;

import java.util.ArrayList;

public class PlayerData {
	
	public PlayerData(String name, ArrayList<Segment> drawing, PlayerColors colors, int index) {
		this.name = name;
		this.drawing = drawing;
		this.colors = colors;
		this.index = index;
	}
	
	public final String name;
	
	/**
	 * AKA: Avatar.
	 */
	public final ArrayList<Segment> drawing;
	
	public final PlayerColors colors;
	
	public final int index;
	
	public String toString() {
		String result = this.name.concat("#");
		for (Segment segment : this.drawing) {
			result = result.concat(segment.toString()).concat("%");
		}
		return result;
	}
}
