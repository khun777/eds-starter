package ch.rasc.eds.starter.service;

import static ch.ralscha.extdirectspring.annotation.ExtDirectMethodType.STORE_MODIFY;
import static ch.ralscha.extdirectspring.annotation.ExtDirectMethodType.STORE_READ;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.validation.Validator;

import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.StringUtils;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreReadRequest;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreResult;
import ch.ralscha.extdirectspring.filter.StringFilter;
import ch.rasc.eds.starter.config.security.JpaUserDetails;
import ch.rasc.eds.starter.dto.UserSettings;
import ch.rasc.eds.starter.entity.QAccessLog;
import ch.rasc.eds.starter.entity.QUser;
import ch.rasc.eds.starter.entity.Role;
import ch.rasc.eds.starter.entity.User;
import ch.rasc.edsutil.QueryUtil;
import ch.rasc.edsutil.ValidationUtil;
import ch.rasc.edsutil.bean.ValidationMessages;
import ch.rasc.edsutil.bean.ValidationMessagesResult;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.SearchResults;
import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.impl.JPAQuery;

@Service
@Lazy
@PreAuthorize("hasRole('ADMIN')")
public class UserService {

	private final MessageSource messageSource;

	private final Validator validator;

	private final EntityManager entityManager;

	private final MailService mailService;

	@Autowired
	public UserService(EntityManager entityManager, Validator validator,
			MessageSource messageSource, MailService mailService) {
		this.entityManager = entityManager;
		this.messageSource = messageSource;
		this.validator = validator;
		this.mailService = mailService;
	}

	@ExtDirectMethod(STORE_READ)
	@Transactional(readOnly = true)
	public ExtDirectStoreResult<User> read(ExtDirectStoreReadRequest request,
			Locale locale) {

		JPQLQuery query = new JPAQuery(entityManager).from(QUser.user);
		if (!request.getFilters().isEmpty()) {
			StringFilter filter = (StringFilter) request.getFilters().iterator().next();

			BooleanBuilder bb = new BooleanBuilder();
			bb.or(QUser.user.email.containsIgnoreCase(filter.getValue()));
			bb.or(QUser.user.lastName.containsIgnoreCase(filter.getValue()));
			bb.or(QUser.user.firstName.containsIgnoreCase(filter.getValue()));
			bb.or(QUser.user.email.containsIgnoreCase(filter.getValue()));

			query.where(bb);
		}
		query.where(QUser.user.deleted.isFalse());

		QueryUtil.addPagingAndSorting(query, request, User.class, QUser.user);
		SearchResults<User> searchResult = query.listResults(QUser.user);

		PrettyTime prettyTime = new PrettyTime(locale);
		for (User u : searchResult.getResults()) {

			LocalDateTime lastAccess = new JPAQuery(entityManager)
					.from(QAccessLog.accessLog)
					.where(QAccessLog.accessLog.email.eq(u.getEmail()))
					.orderBy(QAccessLog.accessLog.loginTimestamp.desc()).limit(1)
					.singleResult(QAccessLog.accessLog.loginTimestamp);
			if (lastAccess != null) {
				u.setLastLogin(prettyTime.format(Date.from(lastAccess.atZone(
						ZoneId.systemDefault()).toInstant())));
			}
			else {
				u.setLastLogin(null);
			}
		}

		return new ExtDirectStoreResult<>(searchResult.getTotal(),
				searchResult.getResults());
	}

	@ExtDirectMethod(STORE_MODIFY)
	@Transactional
	public ExtDirectStoreResult<User> destroy(User destroyUser) {
		ExtDirectStoreResult<User> result = new ExtDirectStoreResult<>();
		if (!isLastAdmin(destroyUser.getId())) {
			User user = entityManager.find(User.class, destroyUser.getId());
			user.setDeleted(true);
			result.setSuccess(Boolean.TRUE);
		}
		else {
			result.setSuccess(Boolean.FALSE);
		}
		return result;
	}

	@ExtDirectMethod(STORE_MODIFY)
	@Transactional
	public ValidationMessagesResult<User> update(User updatedEntity, Locale locale) {
		preModify(updatedEntity);
		List<ValidationMessages> violations = validateEntity(updatedEntity, locale);
		if (violations.isEmpty()) {
			User merged = entityManager.merge(updatedEntity);
			if (updatedEntity.isPasswordReset()) {
				sendPassordResetEmail(merged);
			}

			return new ValidationMessagesResult<>(merged);
		}

		ValidationMessagesResult<User> result = new ValidationMessagesResult<>(
				updatedEntity);
		result.setValidations(violations);
		return result;
	}

	private void sendPassordResetEmail(User user) {
		String token = UUID.randomUUID().toString();
		mailService.sendPasswortResetEmail(user, token);

		user.setPasswordResetTokenValidUntil(LocalDateTime.now().plusHours(4));
		user.setPasswordResetToken(token);
		entityManager.merge(user);
	}

	private List<ValidationMessages> validateEntity(User user, Locale locale) {
		List<ValidationMessages> validations = ValidationUtil.validateEntity(validator,
				user);

		if (emailTaken(user.getId(), user.getEmail())) {
			ValidationMessages validationError = new ValidationMessages();
			validationError.setField("email");
			validationError.setMessage(messageSource.getMessage("user_emailtaken", null,
					locale));
			validations.add(validationError);
		}

		return validations;
	}

	private void preModify(User entity) {
		if (entity.getId() != null && entity.getId() > 0) {
			String dbPassword = new JPAQuery(entityManager).from(QUser.user)
					.where(QUser.user.id.eq(entity.getId()))
					.singleResult(QUser.user.passwordHash);
			entity.setPasswordHash(dbPassword);
		}

	}

	private boolean isLastAdmin(Long id) {
		JPQLQuery query = new JPAQuery(entityManager).from(QUser.user);
		BooleanBuilder bb = new BooleanBuilder();
		bb.or(QUser.user.role.eq(Role.ADMIN.name()));
		bb.or(QUser.user.role.endsWith("," + Role.ADMIN.name()));
		bb.or(QUser.user.role.contains("," + Role.ADMIN.name() + ","));
		bb.or(QUser.user.role.startsWith(Role.ADMIN.name() + ","));

		query.where(QUser.user.id.ne(id).and(QUser.user.deleted.isFalse()).and(bb));
		return query.notExists();
	}

	private boolean emailTaken(Long userId, String email) {
		// Check uniqueness of email
		if (StringUtils.hasText(email)) {
			BooleanBuilder bb = new BooleanBuilder(
					QUser.user.email.equalsIgnoreCase(email));
			if (userId != null) {
				bb.and(QUser.user.id.ne(userId));
			}
			bb.and(QUser.user.deleted.isFalse());
			return new JPAQuery(entityManager).from(QUser.user).where(bb).exists();
		}

		return false;
	}

	@ExtDirectMethod(STORE_READ)
	@PreAuthorize("isAuthenticated()")
	public List<Map<String, String>> readRoles() {
		return Arrays.stream(Role.values())
				.map(r -> Collections.singletonMap("name", r.name()))
				.collect(Collectors.toList());
	}

	@ExtDirectMethod
	@Transactional
	public boolean unlock(Long userId) {
		User user = entityManager.find(User.class, userId);
		if (user != null) {
			user.setLockedOutUntil(null);
			user.setFailedLogins(null);
			entityManager.persist(user);
			return true;
		}
		return false;
	}

	@ExtDirectMethod
	@Transactional(readOnly = true)
	@PreAuthorize("isAuthenticated()")
	public UserSettings readSettings(
			@AuthenticationPrincipal JpaUserDetails jpaUserDetails) {
		return new UserSettings(entityManager.find(User.class,
				jpaUserDetails.getUserDbId()));
	}

	@ExtDirectMethod
	@PreAuthorize("isAuthenticated()")
	@Transactional
	public List<ValidationMessages> updateSettings(UserSettings modifiedUserSettings,
			@AuthenticationPrincipal JpaUserDetails jpaUserDetails, Locale locale) {

		List<ValidationMessages> validations = ValidationUtil.validateEntity(validator,
				modifiedUserSettings);
		User user = entityManager.find(User.class, jpaUserDetails.getUserDbId());

		if (validations.isEmpty()) {
			user.setLastName(modifiedUserSettings.getLastName());
			user.setFirstName(modifiedUserSettings.getFirstName());
			user.setEmail(modifiedUserSettings.getEmail());
			user.setLocale(modifiedUserSettings.getLocale());
		}

		// todo is this neccessary
		if (emailTaken(user.getId(), modifiedUserSettings.getEmail())) {
			ValidationMessages validationError = new ValidationMessages();
			validationError.setField("email");
			validationError.setMessage(messageSource.getMessage("user_emailtaken", null,
					locale));
			validations.add(validationError);
		}

		if (!validations.isEmpty()) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		}
		else {
			if (modifiedUserSettings.isPasswordReset()) {
				sendPassordResetEmail(user);
			}
		}

		return validations;
	}

}
