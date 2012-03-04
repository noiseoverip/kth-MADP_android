package com.madp.utils;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

import com.madp.entities.Meeting;
import com.madp.entities.User;

public class EmailUtils {
	
	private static Logger logger = Logger.getLogger(EmailUtils.class);
	private static final String emailFormat = "You have been invited to meeting: %s\n\nIt is starting at: %s\n\n\nClick on the link below:\nhttp://meetme.com/meeting.meeting?id=%d\n\n\nMeeting was created by %s";
	
	private static String host = "smtp.gmail.com";
	private static int port = 587;
	private static String username = "";
	private static String password = "";
	
	public static void emailUsers(Meeting meeting){
		logger.debug("Sending email to users");
		String body = String.format(emailFormat, meeting.getTitle(), meeting.gettStarting(), meeting.getId(), meeting.getOwner().getEmail());
		
		String[] emails = new String[meeting.getParticipants().size()];
		int i = 0;
		for (User user : meeting.getParticipants()){
			logger.debug("Added email: "+user.getEmail());
			emails[i]=user.getEmail();
			i++;
		}
		
		sendEmail("meetme@meetme.com", emails, "Meeting invitation", body);
	}
	
	public static void sendEmail(String aFromEmailAddr, String[] aToEmailAddr, String aSubject, String aBody) {
		// Get system properties
		Properties props = new Properties();
		props.put("mail.smtp.debug", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", String.valueOf(port));
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");	    
	    
			
		Session session = Session.getInstance(props,new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username,password);
			}
		});
		
		MimeMessage message = new MimeMessage(session);
		
		try {			
			for (String emailTo : aToEmailAddr){
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(emailTo));				
			}			
			message.setSubject(aSubject);
			message.setText(aBody);
			
			Transport transport = session.getTransport("smtp");
			transport.connect(host, port, username, password);			
			Transport.send(message);
			} catch (MessagingException ex) {
			
			logger.error("Cannot send email to "+aToEmailAddr,ex);
		}
	}	
}
