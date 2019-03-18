package mailServer;

import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.Message.RecipientType;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.search.FlagTerm;

import basis.BasisHandler;
import basis.helpers.BasisLiveData;
import exceptions.BasisException;
import mailServer.helpers.MailMessage;

/**
 * 
 * @author adriaanpeetermans
 *
 *	Possible messages:
 *		BASIS get live				Returns live data from BASIS station.
 */
public class MailServer extends Thread {
	
	public MailServer(int refreshSeconds, String mailAddress, String password, String userName, String basisIP, int basisPort, int basisMaxDays) {
		this.refreshSeconds = refreshSeconds;
		this.mailAddress = mailAddress;
		this.password = password;
		this.userName = userName;
		this.nextRefresh = (int) (System.currentTimeMillis() / 1000L);
		this.bh = new BasisHandler(basisIP, basisPort, basisMaxDays, 10, 5);
	}
	
	public final int refreshSeconds;
	
	private final String mailAddress;
	
	private final String password;
	
	private int nextRefresh;
	
	private BasisHandler bh;
	
	private final String userName;
	
	public void run() {
		while (true) {
			int time = (int) (System.currentTimeMillis() / 1000L);
			if (time >= this.nextRefresh) {
				this.nextRefresh = this.nextRefresh + this.refreshSeconds;
				HashSet<MailMessage> mails = this.getMails();
				for (MailMessage mail : mails) {
					this.parseMail(mail);
				}
			}
		}
	}
	
	private void parseMail(MailMessage mail) {
		String content = mail.content;
		String[] lines = content.split("\n");
		String answer = "----- WeatherStation response -----\n";
		boolean wrongLine = false;
		for (String line : lines) {
			System.out.println("Line: ".concat(line));
			System.out.println(line.split("\r"));
			String l = line.split("\r")[0];
			switch (l) {
				case "BASIS get live":
					answer = answer.concat(this.getLive()).concat("\n");
					break;
				case "BASIS help":
				case "BASIS Help":
				case "BASIS HELP":
				case "help":
				case "Help":
				case "HELP":
					answer = answer.concat(this.getHelp()).concat("\n");
					break;
				default:
					wrongLine = true;
					break;
			}
		}
		if (wrongLine) {
			answer = answer.concat(this.getWrongLine()).concat("\n");
		}
		this.sendMail(mail.from, "RE: ".concat(mail.subject), answer);
	}
	
	private String getLive() {
		BasisLiveData data = null;
		try {
			data = bh.getLiveData();
			
		} catch (BasisException e) {
			System.out.println("kan niet verbinden...");
			e.printStackTrace();
		}
		//data = new BasisLiveData(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 19, Calendar.getInstance());
		String result = "BASIS live data:\n";
		if (data == null) {
			result = result.concat("No live data available.\n");
			return result;
		}
		result = result.concat("Temperature outside:\t\t").concat(Float.toString(data.tempOut)).concat("°C").concat("\n");
		result = result.concat("Temperature inside:\t\t").concat(Float.toString(data.tempIn)).concat("°C").concat("\n");
		result = result.concat("Temperature greenery:\t\t").concat(Float.toString(data.tempSer)).concat("°C").concat("\n");
		result = result.concat("Relative moisture outside:\t").concat(Float.toString(data.moistOut)).concat("%").concat("\n");
		result = result.concat("Relative moisture inside:\t").concat(Float.toString(data.moistIn)).concat("%").concat("\n");
		result = result.concat("Relative moisture greenery:\t").concat(Float.toString(data.moistSer)).concat("%").concat("\n");
		result = result.concat("Air pressure:\t\t\t").concat(Float.toString(data.press)).concat(" hPa").concat("\n");
		result = result.concat("Light level outside:\t\t").concat(Float.toString(data.lightOut)).concat("\n");
		result = result.concat("Light level inside:\t\t").concat(Float.toString(data.lightIn)).concat("\n");
		result = result.concat("Panel voltage:\t\t\t").concat(Float.toString(data.pv)).concat(" V").concat("\n");
		result = result.concat("Battery voltage:\t\t").concat(Float.toString(data.bv)).concat(" V").concat("\n");
		return result;
	}
	
	private String getHelp() {
		String result = "This is the weatherStation mail interface.\nPossible commands (so far) are:\n"
				+ "\tBASIS get live\t\t\tGet live data from the weather station.\n"
				+ "\tBASIS help\t\t\tShow this help message.\n\n"
				+ "Creator: Adriaan Peetermans\tLast update on: 24/02/2019\n";
		return result;
	}
	
	private String getWrongLine() {
		String result = "One or multiple commands you entered are unvalid.\n"
				+ "Auto generated help message:\n";
		result = result.concat(this.getHelp());
		return result;
	}
	
	//Date format: 24/02/2019
//	private string getFile(String data) {
//		String[] dataParts = data.split("/");
//		if (dataParts.length != 3) {
//			return "Wrong date format!\n";
//		}
//		int day, month, year;
//		try {
//			day = Integer.parseInt(dataParts[0]);
//			month = Integer.parseInt(dataParts[1]);
//			year = Integer.parseInt(dataParts[2]);
//		}
//		catch (NumberFormatException e) {
//			return "Wrong date format!\n";
//		}
//		Parser basisParser = new Parser("BASIS", year, month, day, true);
//		BasisData basisData = (BasisData) basisParser.readFile();
//		Parser sensor1Parser = new Parser("SENSOR1", year, month, day, true);
//		Sensor1Data sensor1Date = (Sensor1Data) sensor1Parser.readFile();
//	}
	
	private HashSet<MailMessage> getMails() {
		HashSet<MailMessage> result = new HashSet<MailMessage>(10);
        String receivingHost = "imap.gmail.com";
        Properties props2 = System.getProperties();
        props2.setProperty("mail.store.protocol", "imaps");
        Session session2=Session.getDefaultInstance(props2, null);
        try {
    		Store store = session2.getStore("imaps");
    		store.connect(receivingHost, this.userName, this.password);
    		Folder folder = store.getFolder("INBOX");
			folder.open(Folder.READ_WRITE);
			Message message[] = folder.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
			for(int i = 0; i < message.length; i++){
				String mailContent = this.getText(message[i]);
				if (this.textIsHtml) {
					mailContent = this.convertHTML(mailContent);
				}
				Address[] froms = message[i].getFrom();
                String email = froms == null ? null : ((InternetAddress) froms[0]).getAddress();
				MailMessage mail = new MailMessage(email, mailContent, message[i].getSubject());
				result.add(mail);
			}
			folder.close(true);
			store.close();
            }
    	catch (Exception e) {
			System.out.println(e.toString());
    	}
        return result;
	}
	
	private String convertHTML(String text) {
		String result = "";
		String[] pieces = text.split("</div>");
		for (int i = 0; i < pieces.length - 1; i++) {
			String[] piecess = pieces[i].split(">");
			result = result.concat(this.removeNewLine(piecess[piecess.length-1])).concat("\n");
		}
		return result;
	}
	
	private String removeNewLine(String text) {
		int i = text.length() - 1;
		while (i >= 0) {
			if ((text.charAt(i) == '\n') || (text.charAt(i) == '\r')) {
				text = text.substring(0, i).concat(text.substring(i+1,text.length()));
			}
			i --;
		}
		return text;
	}
	
	private void sendMail(String to, String subject, String text) {
		String sendingHost = "smtp.gmail.com";
		int sendingPort = 465;
		Properties props = new Properties();
		props.put("mail.smtp.host", sendingHost);	
		props.put("mail.smtp.port", String.valueOf(sendingPort));
		props.put("mail.smtp.user", this.userName);
		props.put("mail.smtp.password", this.password);
		props.put("mail.smtp.auth", "true");
		Session session1 = Session.getDefaultInstance(props);
		Message simpleMessage = new MimeMessage(session1);
		InternetAddress fromAddress = null;
		InternetAddress toAddress = null;
		try {
			fromAddress = new InternetAddress(this.mailAddress);
			toAddress = new InternetAddress(to);
		}
		catch (AddressException e) {
			e.printStackTrace();
		}
		try {
			simpleMessage.setFrom(fromAddress);
			simpleMessage.setRecipient(RecipientType.TO, toAddress);
			simpleMessage.setSubject(subject);
			simpleMessage.setText(text);
			Transport transport = session1.getTransport("smtps");
			transport.connect (sendingHost,sendingPort, this.userName, this.password);
			transport.sendMessage(simpleMessage, simpleMessage.getAllRecipients());
			transport.close();
		}
		catch (MessagingException e) {
			e.printStackTrace();
		}
	}
	
	private boolean textIsHtml = false;
	
	private String getText(Part p) throws MessagingException, IOException {
        if (p.isMimeType("text/*")) {
            String s = (String) p.getContent();
            textIsHtml = p.isMimeType("text/html");
            return s;
        }
        if (p.isMimeType("multipart/alternative")) {
            // prefer html text over plain text
            Multipart mp = (Multipart) p.getContent();
            String text = null;
            for (int i = 0; i < mp.getCount(); i++) {
                Part bp = mp.getBodyPart(i);
                if (bp.isMimeType("text/plain")) {
                    if (text == null) {
                        text = getText(bp);
                    }
                    continue;
                }
                else {
                	if (bp.isMimeType("text/html")) {
                		String s = getText(bp);
                		if (s != null) {
                			return s;
                		}
                	}
                	else {
                		return getText(bp);
                	}
                }
            }
            return text;
        }
        else {
        	if (p.isMimeType("multipart/*")) {
        		Multipart mp = (Multipart) p.getContent();
        		for (int i = 0; i < mp.getCount(); i++) {
        			String s = getText(mp.getBodyPart(i));
        			if (s != null) {
        				return s;
        			}
        		}
        	}
        }
        return null;
    }
}
