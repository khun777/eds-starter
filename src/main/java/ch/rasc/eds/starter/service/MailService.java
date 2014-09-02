package ch.rasc.eds.starter.service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import ch.rasc.eds.starter.config.EmailProperties;

@Service
public class MailService {

	private final JavaMailSenderImpl mailSender;
	private final String defaultSender;

	@Autowired
	public MailService(JavaMailSenderImpl mailSender, EmailProperties emailProperties) {
		this.mailSender = mailSender;
		this.defaultSender = emailProperties.getSender();
	}

	@Async
	public void sendSimpleMessage(String to, String subject, String text) {
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setFrom(defaultSender);
		mailMessage.setTo(to);
		mailMessage.setText(text);
		mailMessage.setSubject(subject);

		mailSender.send(mailMessage);
	}

	@Async
	public void sendHtmlMessage(String from, String to, String subject, String text)
			throws MessagingException {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		helper.setFrom(defaultSender);
		helper.setTo(to);
		helper.setReplyTo(from);
		helper.setText(text, true);
		helper.setSubject(subject);

		mailSender.send(message);
	}

}
