package testers;

import dataBase.helpers.BasisData;

public class BasisData_tb {

	public static void main(String[] args) {
		BasisData reader = new BasisData(14, 11, 2018);
		reader.parse();
		System.out.println(reader.time.get(0));
		System.out.println(reader.light.get(0));
		System.out.println(reader.moisture.get(0));
		System.out.println(reader.light.size());
		System.out.println(reader.light.get(287));
		System.out.println(reader.time.get(287));
	}

}
