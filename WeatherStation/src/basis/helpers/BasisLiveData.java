package basis.helpers;

import java.util.Calendar;

public class BasisLiveData {
	
	public BasisLiveData(float tempOut, float tempSer, float tempIn, float moistOut, float moistSer, float moistIn, float press, int lightOut, int lightIn, float pv, float bv, Calendar time) {
		this.tempOut = tempOut;
		this.tempSer = tempSer;
		this.tempIn = tempIn;
		this.moistOut = moistOut;
		this.moistSer = moistSer;
		this.moistIn = moistIn;
		this.press = press;
		this.lightOut = lightOut;
		this.lightIn = lightIn;
		this.pv = pv;
		this.bv = bv;
		this.time = time;
	}
	
	public final float tempOut;
	
	public final float tempSer;
	
	public final float tempIn;
	
	public final float moistOut;
	
	public final float moistSer;
	
	public final float moistIn;
	
	public final float press;
	
	public final int lightOut;
	
	public final int lightIn;
	
	public final float pv;
	
	public final float bv;
	
	public final Calendar time;
}
