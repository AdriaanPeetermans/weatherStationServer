package helpers.playerData;

import java.util.ArrayList;

public class PlayerData {
	
	public PlayerData(String name, ArrayList<Segment> drawing, PlayerColors colors) {
		this.name = name;
		this.drawing = drawing;
		this.colors = colors;
	}
	
	public final String name;
	
	public final ArrayList<Segment> drawing;
	
	public final PlayerColors colors;
}
