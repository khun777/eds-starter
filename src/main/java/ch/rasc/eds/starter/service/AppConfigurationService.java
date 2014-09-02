package ch.rasc.eds.starter.service;

import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.rasc.eds.starter.config.security.UserAuthenticationErrorHandler;
import ch.rasc.eds.starter.dto.ConfigurationDto;
import ch.rasc.eds.starter.entity.Configuration;
import ch.rasc.eds.starter.entity.ConfigurationKey;
import ch.rasc.eds.starter.repository.ConfigurationRepository;

@Service
@Lazy
public class AppConfigurationService {

	private final MailService mailService;

	private final ConfigurationRepository configurationRepository;

	private final UserAuthenticationErrorHandler userAuthenticationErrorHandler;

	@Autowired
	public AppConfigurationService(ConfigurationRepository configurationRepository,
			MailService mailService,
			UserAuthenticationErrorHandler userAuthenticationErrorHandler) {
		this.mailService = mailService;
		this.configurationRepository = configurationRepository;
		this.userAuthenticationErrorHandler = userAuthenticationErrorHandler;
	}

	@ExtDirectMethod
	@PreAuthorize("hasRole('ADMIN')")
	public void sendTestEmail(String to) {
		mailService.sendSimpleMessage(to, "TEST EMAIL", "THIS IS A TEST MESSAGE");
	}

	@ExtDirectMethod
	@PreAuthorize("hasRole('ADMIN')")
	public ConfigurationDto read() {

		ConfigurationDto dto = new ConfigurationDto();

		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		ch.qos.logback.classic.Logger logger = lc.getLogger("ch.rasc.eds.starter");
		String level = logger != null && logger.getEffectiveLevel() != null ? logger
				.getEffectiveLevel().toString() : "ERROR";
		dto.setLogLevel(level);

		List<Configuration> configurations = configurationRepository.findAll();

		String value = read(ConfigurationKey.LOGIN_LOCK_ATTEMPTS, configurations);
		if (value != null) {
			dto.setLoginLockAttempts(Integer.valueOf(value));
		}

		value = read(ConfigurationKey.LOGIN_LOCK_MINUTES, configurations);
		if (value != null) {
			dto.setLoginLockMinutes(Integer.valueOf(value));
		}

		return dto;
	}

	private static String read(ConfigurationKey key, List<Configuration> list) {
		for (Configuration conf : list) {
			if (conf.getConfKey() == key) {
				return conf.getConfValue();
			}
		}
		return null;
	}

	@ExtDirectMethod
	@PreAuthorize("hasRole('ADMIN')")
	public void save(ConfigurationDto data) {
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		ch.qos.logback.classic.Logger logger = lc.getLogger("ch.rasc.eds.starter");
		Level level = Level.toLevel(data.getLogLevel());
		if (level != null) {
			logger.setLevel(level);
		}

		Integer value = data.getLoginLockAttempts() != null ? Integer.valueOf(data
				.getLoginLockAttempts()) : null;
		update(ConfigurationKey.LOGIN_LOCK_ATTEMPTS, value != null ? value.toString()
				: null);

		value = data.getLoginLockMinutes() != null ? Integer.valueOf(data
				.getLoginLockMinutes()) : null;
		update(ConfigurationKey.LOGIN_LOCK_MINUTES, value != null ? value.toString()
				: null);

		userAuthenticationErrorHandler.configure(configurationRepository);
	}

	private void update(ConfigurationKey key, String value) {
		Configuration conf = configurationRepository.findByConfKey(key);

		if (StringUtils.hasText(value)) {
			if (conf != null) {
				conf.setConfValue(value);
			}
			else {
				conf = new Configuration();
				conf.setConfKey(key);
				conf.setConfValue(value);
			}

			configurationRepository.save(conf);
		}
		else {
			if (conf != null) {
				configurationRepository.delete(conf);
			}
		}

	}

}
