package ch.rasc.eds.starter.config.security;

import java.time.LocalDateTime;

import javax.persistence.EntityManager;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import ch.rasc.eds.starter.entity.Configuration;
import ch.rasc.eds.starter.entity.ConfigurationKey;
import ch.rasc.eds.starter.entity.QConfiguration;
import ch.rasc.eds.starter.entity.QUser;
import ch.rasc.eds.starter.entity.User;

import com.mysema.query.jpa.impl.JPAQuery;

@Component
public class UserAuthenticationErrorHandler implements
		ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

	private final EntityManager entityManager;

	private Integer loginLockAttempts;

	private Integer loginLockMinutes;

	private final PlatformTransactionManager transactionManager;

	@Autowired
	public UserAuthenticationErrorHandler(EntityManager entityManager,
			PlatformTransactionManager transactionManager) {
		this.entityManager = entityManager;
		this.transactionManager = transactionManager;

		new TransactionTemplate(transactionManager)
				.execute(new TransactionCallbackWithoutResult() {
					@Override
					protected void doInTransactionWithoutResult(TransactionStatus status) {
						configure();
					}
				});
	}

	public void configure() {
		Configuration conf = readConfiguration(ConfigurationKey.LOGIN_LOCK_ATTEMPTS);
		if (conf != null && conf.getConfValue() != null) {
			loginLockAttempts = Integer.valueOf(conf.getConfValue());
		}
		else {
			loginLockAttempts = null;
		}

		conf = readConfiguration(ConfigurationKey.LOGIN_LOCK_MINUTES);
		if (conf != null && conf.getConfValue() != null) {
			loginLockMinutes = Integer.valueOf(conf.getConfValue());
		}
		else {
			loginLockMinutes = null;
		}
	}

	public Configuration readConfiguration(ConfigurationKey key) {
		return new JPAQuery(entityManager).from(QConfiguration.configuration)
				.where(QConfiguration.configuration.confKey.eq(key))
				.singleResult(QConfiguration.configuration);
	}

	@Override
	public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
		new TransactionTemplate(transactionManager)
				.execute(new TransactionCallbackWithoutResult() {
					@Override
					protected void doInTransactionWithoutResult(TransactionStatus status) {
						updateLockedProperties(event);
					}
				});
	}

	private void updateLockedProperties(AuthenticationFailureBadCredentialsEvent event) {
		Object principal = event.getAuthentication().getPrincipal();

		if (loginLockAttempts != null && principal instanceof String) {
			User user = new JPAQuery(entityManager).from(QUser.user)
					.where(QUser.user.email.eq((String) principal))
					.singleResult(QUser.user);
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
				entityManager.merge(user);
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
