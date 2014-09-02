package ch.rasc.eds.starter.config.security;

import java.time.LocalDateTime;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

import ch.rasc.eds.starter.entity.Configuration;
import ch.rasc.eds.starter.entity.ConfigurationKey;
import ch.rasc.eds.starter.entity.User;
import ch.rasc.eds.starter.repository.ConfigurationRepository;
import ch.rasc.eds.starter.repository.UserRepository;

@Component
public class UserAuthenticationErrorHandler implements
		ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

	private final UserRepository userRepository;

	private Integer loginLockAttempts;

	private Integer loginLockMinutes;

	@Autowired
	public UserAuthenticationErrorHandler(UserRepository userRepository,
			ConfigurationRepository configurationRepository) {
		this.userRepository = userRepository;
		configure(configurationRepository);
	}

	public void configure(ConfigurationRepository configurationRepository) {
		Configuration conf = configurationRepository
				.findByConfKey(ConfigurationKey.LOGIN_LOCK_ATTEMPTS);
		if (conf != null && conf.getConfValue() != null) {
			loginLockAttempts = Integer.valueOf(conf.getConfValue());
		}
		else {
			loginLockAttempts = null;
		}

		conf = configurationRepository.findByConfKey(ConfigurationKey.LOGIN_LOCK_MINUTES);
		if (conf != null && conf.getConfValue() != null) {
			loginLockMinutes = Integer.valueOf(conf.getConfValue());
		}
		else {
			loginLockMinutes = null;
		}
	}

	@Override
	public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
		Object principal = event.getAuthentication().getPrincipal();

		if (loginLockAttempts != null && principal instanceof String) {
			User user = userRepository.findByEmail((String) principal);
			if (user != null) {
				if (user.getFailedLogins() == null) {
					user.setFailedLogins(1);
				}
				else {
					user.setFailedLogins(user.getFailedLogins() + 1);
				}

				if (user.getFailedLogins() >= loginLockAttempts) {
					if (loginLockMinutes != null) {
						user.setLockedOutUntil(LocalDateTime.now().plusMinutes(
								loginLockMinutes));
					}
					else {
						user.setLockedOutUntil(LocalDateTime.now().plusYears(1000));
					}
				}
				userRepository.save(user);
			}
			else {
				LoggerFactory.getLogger(UserAuthenticationErrorHandler.class).warn(
						"Unknown user login attempt: {}", principal);
			}
		}
		else {
			LoggerFactory.getLogger(UserAuthenticationErrorHandler.class).warn(
					"Invalid login attempt: {}", principal);
		}

	}
}