package testers;

import basis.SingleMessageConnector;

public class SingleMessageConnector_tb {

	public static void main(String[] args) {
		SingleMessageConnector smc = new SingleMessageConnector("192.168.1.58", 9876, 1, 2, 3);
		System.out.println(smc.sendSingleMessage("100 333#20.1#55.34#22.1#1025.34#5.01#3.76"));		
	}
}
