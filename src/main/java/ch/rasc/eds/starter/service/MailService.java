package ch.rasc.eds.starter.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.unbescape.html.HtmlEscape;

import ch.rasc.eds.starter.config.AppProperties;

@Service
public class MailService {

	private final JavaMailSender mailSender;
	private final String defaultSender;

	private final String passwordResetEmail;

	private final AppProperties appProperties;

	@Autowired
	public MailService(JavaMailSender mailSender, 
			AppProperties appProperties) throws IOException {
		this.mailSender = mailSender;
		this.defaultSender = appProperties.getDefaultEmailSender();
		this.appProperties = appProperties;

		ClassPathResource cp = new ClassPathResource("password_reset_email.txt");
		try (InputStream is = cp.getInputStream()) {
			String text = StreamUtils.copyToString(is, StandardCharsets.UTF_8);
			text = HtmlEscape.escapeHtml4(text);
			passwordResetEmail = text;
		}
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
	public void sendPasswortResetEmail(String to, String token) {
		String link = appProperties.getUrl() + "/passwordreset.html?token="
				+ Base64.getUrlEncoder().encodeToString(token.getBytes());

		try {
			sendHtmlMessage(
					defaultSender,
					to,
					"Resor: Passwort zur�ckgesetzt",
					passwordResetEmail
							.replace("{login}", "<strong>" + to + "</strong>")
							.replace("\n", "<br>")
							.replace("{link}",
									"<a href=\"" + link + "\">" + link + "</a>"));
		}
		catch (MessagingException e) {
			LoggerFactory.getLogger(MailService.class).error("sendPasswortResetEmail", e);
		}
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
