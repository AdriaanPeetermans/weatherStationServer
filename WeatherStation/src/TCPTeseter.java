import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class TCPTeseter {

	public static void main(String[] args) {
		String sentence;
		String modifiedSentence;
		//BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		try {
		Socket clientSocket = new Socket("192.168.1.61", 9876);
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		sentence = "P00 2154 3011118";
		outToServer.writeBytes(sentence);
		modifiedSentence = inFromServer.readLine();
		System.out.println("FROM SERVER: " + modifiedSentence);
		clientSocket.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

}
