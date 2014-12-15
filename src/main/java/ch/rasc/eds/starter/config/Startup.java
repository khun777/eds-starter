package ch.rasc.eds.starter.config;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.db.DBAppender;
import ch.qos.logback.core.db.DataSourceConnectionSource;
import ch.rasc.eds.starter.entity.QUser;
import ch.rasc.eds.starter.entity.Role;
import ch.rasc.eds.starter.entity.User;

import com.mysema.query.jpa.impl.JPAQuery;

@Component
class Startup {

	private final EntityManager entityManager;

	private final DataSource dataSource;

	private final PasswordEncoder passwordEncoder;

	@Autowired
	public Startup(EntityManager entityManager, DataSource dataSource,
			PasswordEncoder passwordEncoder, PlatformTransactionManager transactionManager) {
		this.entityManager = entityManager;
		this.dataSource = dataSource;
		this.passwordEncoder = passwordEncoder;

		new TransactionTemplate(transactionManager)
				.execute(new TransactionCallbackWithoutResult() {
					@Override
					protected void doInTransactionWithoutResult(TransactionStatus status) {
						init();
					}
				});

	}

	private void init() {

		configureLog();

		if (new JPAQuery(entityManager).from(QUser.user).count() == 0) {
			// admin user
			User adminUser = new User();
			adminUser.setEmail("admin@starter.com");
			adminUser.setFirstName("admin");
			adminUser.setLastName("admin");
			adminUser.setLocale("en");
			adminUser.setPasswordHash(passwordEncoder.encode("admin"));
			adminUser.setEnabled(true);
			adminUser.setDeleted(false);
			adminUser.setRole(Role.ADMIN.name());
			entityManager.persist(adminUser);

			// normal user
			User normalUser = new User();
			normalUser.setEmail("user@starter.com");
			normalUser.setFirstName("user");
			normalUser.setLastName("user");
			normalUser.setLocale("de");
			normalUser.setPasswordHash(passwordEncoder.encode("user"));
			normalUser.setEnabled(true);
			adminUser.setDeleted(false);
			normalUser.setRole(Role.USER.name());
			entityManager.persist(normalUser);
		}

	}

	private void configureLog() {

		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		ch.qos.logback.classic.Logger logger = lc.getLogger(Logger.ROOT_LOGGER_NAME);

		if (logger.getAppender("DB") == null) {
			DBAppender appender = new DBAppender();
			appender.setName("DB");
			appender.setContext(lc);

			DataSourceConnectionSource cs = new DataSourceConnectionSource();
			cs.setDataSource(dataSource);
			cs.setContext(lc);
			cs.start();

			appender.setConnectionSource(cs);
			appender.start();

			logger.addAppender(appender);
		}

	}

}
