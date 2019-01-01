package siteServer.helpers;

public abstract class Escaper {
	
	public static String escape(String in) {
		String out = "";
		for (int i = 0; i < in.length(); i ++) {
			//Spatie = %S
			if (in.charAt(i) == " ".charAt(0)) {
				out = out + "%S";
			}
			//# = %N
			else if (in.charAt(i) == "#".charAt(0)) {
				out = out + "%N";
			}
			//% = %%
			else if (in.charAt(i) == "%".charAt(0)) {
				out = out + "%%";
			}
			//Geen probleem
			else {
				out = out + in.substring(i, i + 1);
			}
		}
		return out;
	}
	
	public static String unEscape(String in) {
		String out = "";
		int i = 0;
		while (i < in.length()) {
			if (in.charAt(i) == "%".charAt(0)) {
				i ++;
				//Spatie
				if (in.charAt(i) == "S".charAt(0)) {
					out = out + " ";
				}
				//#
				else if (in.charAt(i) == "N".charAt(0)) {
					out = out + "#";
				}
				//Moet % zijn
				else {
					out = out + "%";
				}
			}
			//Geen probleem
			else {
				out = out + in.substring(i, i + 1);
			}
			i ++;
		}
		return out;
	}
	
	public static byte[] stringToBytes(String mes) {
		byte[] result = new byte[mes.length() / 3];
		int index = 0;
		for (int i = 0; i < mes.length(); i = i + 3) {
			int num = Integer.parseInt(mes.substring(i, i + 3));
			result[index] = (byte) num;
			index ++;
		}
		return result;
	}
}
