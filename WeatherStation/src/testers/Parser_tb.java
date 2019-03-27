package testers;

import dataBase.Parser;

public class Parser_tb {

	public static void main(String[] args) {
		Parser parser = new Parser("SENSOR1", 2019, 3, 25);
		parser.appendFile("Een beetje data\r\n");
	}
}
