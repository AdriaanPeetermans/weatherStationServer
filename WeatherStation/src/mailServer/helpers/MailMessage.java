package mailServer.helpers;

public class MailMessage {
	
	public MailMessage(String from, String content, String subject) {
		this.from = from;
		this.content = content;
		if (subject == null) {
			this.subject = "No subject";
		}
		else {
			this.subject = subject;
		}
	}
	
	public final String from;
	
	public final String content;
	
	public final String subject;
}
