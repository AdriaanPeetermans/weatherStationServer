package mailServer.helpers;

public class MailMessage {
	
	public MailMessage(String from, String content, String subject) {
		this.from = from;
		this.content = content;
		this.subject = subject;
	}
	
	public final String from;
	
	public final String content;
	
	public final String subject;
}
