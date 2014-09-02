package ch.rasc.eds.starter.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class EmailConfiguration {

	@Autowired
	private EmailProperties emailProperties;

	@Bean
	public JavaMailSenderImpl javaMailSender() {

		JavaMailSenderImpl sender = new JavaMailSenderImpl();

		sender.setHost(emailProperties.getHost());

		if (emailProperties.getPort() != null) {
			sender.setPort(emailProperties.getPort());
		}

		if (emailProperties.getUsername() != null) {
			sender.setUsername(emailProperties.getUsername());
		}

		if (emailProperties.getPassword() != null) {
			sender.setPassword(emailProperties.getPassword());
		}

		if (emailProperties.getProtocol() != null) {
			sender.setProtocol(emailProperties.getProtocol());
		}

		Properties javaMailProperties = new Properties();
		if (emailProperties.getStarttls() != null) {
			javaMailProperties.put("mail.smtp.starttls.enable",
					Boolean.valueOf(emailProperties.getStarttls()).toString());
		}
		sender.setJavaMailProperties(javaMailProperties);

		return sender;
		//
		// sendProperties.setProperty(PROP_SMTP_AUTH, auth.toString());
		// sendProperties.setProperty(PROP_STARTTLS, tls.toString());
		// sendProperties.setProperty(PROP_TRANSPORT_PROTO, protocol);
		// sender.setJavaMailProperties(sendProperties);
		//
		//
		// public void configure() {
		// List<Configuration> configurations = configurationRepository.findAll();
		//
		// mailSender = new JavaMailSenderImpl();
		// mailSender.setHost(read(ConfigurationKey.SMTP_SERVER, configurations));
		// String portString = read(ConfigurationKey.SMTP_PORT, configurations);
		// if (StringUtils.hasText(portString)) {
		// mailSender.setPort(Integer.parseInt(portString));
		// }
		// mailSender.setUsername(read(ConfigurationKey.SMTP_USERNAME, configurations));
		// mailSender.setPassword(read(ConfigurationKey.SMTP_PASSWORD, configurations));
		//
		// defaultSender = read(ConfigurationKey.SMTP_SENDER, configurations);
		// }

		// private static String read(ConfigurationKey key, List<Configuration> list) {
		// for (Configuration conf : list) {
		// if (conf.getConfKey() == key) {
		// return conf.getConfValue();
		// }
		// }
		// return null;
		// }

		// String host = propertyResolver.getProperty(PROP_HOST, DEFAULT_PROP_HOST);
		// int port = propertyResolver.getProperty(PROP_PORT, Integer.class, 0);
		// String user = propertyResolver.getProperty(PROP_USER);
		// String password = propertyResolver.getProperty(PROP_PASSWORD);
		// String protocol = propertyResolver.getProperty(PROP_PROTO);
		// Boolean tls = propertyResolver.getProperty(PROP_TLS, Boolean.class, false);
		// Boolean auth = propertyResolver.getProperty(PROP_AUTH, Boolean.class, false);
		//
		// JavaMailSenderImpl sender = new JavaMailSenderImpl();
		// if (host != null && !host.isEmpty()) {
		// sender.setHost(host);
		// } else {
		// log.warn("Warning! Your SMTP server is not configured. We will try to use one on localhost.");
		// log.debug("Did you configure your SMTP settings in your application.yml?");
		// sender.setHost(DEFAULT_HOST);
		// }
		// sender.setPort(port);
		// sender.setUsername(user);
		// sender.setPassword(password);
		//
		// Properties sendProperties = new Properties();
		// sendProperties.setProperty(PROP_SMTP_AUTH, auth.toString());
		// sendProperties.setProperty(PROP_STARTTLS, tls.toString());
		// sendProperties.setProperty(PROP_TRANSPORT_PROTO, protocol);
		// sender.setJavaMailProperties(sendProperties);
		// return sender;
	}

}
