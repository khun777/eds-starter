package ch.rasc.eds.starter.service;

import static ch.ralscha.extdirectspring.annotation.ExtDirectMethodType.STORE_MODIFY;
import static ch.ralscha.extdirectspring.annotation.ExtDirectMethodType.STORE_READ;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreReadRequest;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreResult;
import ch.ralscha.extdirectspring.filter.StringFilter;
import ch.rasc.eds.starter.config.security.JpaUserDetails;
import ch.rasc.eds.starter.dto.UserSettings;
import ch.rasc.eds.starter.entity.QUser;
import ch.rasc.eds.starter.entity.Role;
import ch.rasc.eds.starter.entity.User;
import ch.rasc.eds.starter.repository.UserRepository;
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

	private final UserRepository userRepository;

	private final MessageSource messageSource;

	private final PasswordEncoder passwordEncoder;

	private final Validator validator;

	private final EntityManager entityManager;

	@Autowired
	public UserService(EntityManager entityManager, UserRepository userRepository,
			PasswordEncoder passwordEncoder, Validator validator,
			MessageSource messageSource) {
		this.entityManager = entityManager;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.messageSource = messageSource;
		this.validator = validator;
	}

	@ExtDirectMethod(STORE_READ)
	@Transactional(readOnly = true)
	public ExtDirectStoreResult<User> read(ExtDirectStoreReadRequest request) {

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

		QueryUtil.addPagingAndSorting(query, request, User.class, QUser.user);
		SearchResults<User> searchResult = query.listResults(QUser.user);

		return new ExtDirectStoreResult<>(searchResult.getTotal(),
				searchResult.getResults());
	}

	@ExtDirectMethod(STORE_MODIFY)
	@Transactional
	public ExtDirectStoreResult<User> destroy(Long id) {
		ExtDirectStoreResult<User> result = new ExtDirectStoreResult<>();
		if (!isLastAdmin(id)) {
			userRepository.delete(id);
			result.setSuccess(Boolean.TRUE);
		}
		else {
			result.setSuccess(Boolean.FALSE);
		}
		return result;
	}

	@ExtDirectMethod(STORE_MODIFY)
	@Transactional
	public ValidationMessagesResult<User> create(User newEntity, Locale locale) {
		preModify(newEntity);
		List<ValidationMessages> violations = validateEntity(newEntity, locale);
		if (violations.isEmpty()) {
			User saveUser = userRepository.save(newEntity);
			return new ValidationMessagesResult<>(saveUser);
		}

		ValidationMessagesResult<User> result = new ValidationMessagesResult<>(newEntity);
		result.setValidations(violations);
		return result;

	}

	@ExtDirectMethod(STORE_MODIFY)
	@Transactional
	public ValidationMessagesResult<User> update(User updatedEntity, Locale locale) {
		preModify(updatedEntity);
		List<ValidationMessages> violations = validateEntity(updatedEntity, locale);
		if (violations.isEmpty()) {

			return new ValidationMessagesResult<>(userRepository.save(updatedEntity));
		}

		ValidationMessagesResult<User> result = new ValidationMessagesResult<>(
				updatedEntity);
		result.setValidations(violations);
		return result;
	}

	protected List<ValidationMessages> validateEntity(User user, Locale locale) {
		List<ValidationMessages> validations = ValidationUtil.validateEntity(validator,
				user);

		if (emailTaken(user.getId(), user.getEmail())) {
			ValidationMessages validationError = new ValidationMessages();
			validationError.setField("email");
			validationError.setMessage(messageSource.getMessage("user_emailtaken", null,
					locale));
			validations.add(validationError);
		}

		if (StringUtils.hasText(user.getNewPassword())) {
			if (!user.getNewPassword().equals(user.getNewPasswordRetype())) {
				for (String field : new String[] { "newPassword", "newPasswordRetype" }) {
					ValidationMessages error = new ValidationMessages();
					error.setField(field);
					error.setMessage(messageSource.getMessage(
							"user_pwdonotmatch", null, locale));
					validations.add(error);
				}
			}
		}

		return validations;
	}

	private void preModify(User entity) {

		if (StringUtils.hasText(entity.getNewPassword())) {
			entity.setPasswordHash(passwordEncoder.encode(entity.getNewPassword()));
		}
		else {
			if (entity.getId() != null) {
				String dbPassword = new JPAQuery(entityManager).from(QUser.user)
						.where(QUser.user.id.eq(entity.getId()))
						.singleResult(QUser.user.passwordHash);
				entity.setPasswordHash(dbPassword);
			}
		}
	}

	private boolean isLastAdmin(Long id) {
		JPQLQuery query = new JPAQuery(entityManager).from(QUser.user);
		BooleanBuilder bb = new BooleanBuilder();
		bb.or(QUser.user.role.eq(Role.ADMIN.name()));
		bb.or(QUser.user.role.endsWith("," + Role.ADMIN.name()));
		bb.or(QUser.user.role.contains("," + Role.ADMIN.name() + ","));
		bb.or(QUser.user.role.startsWith(Role.ADMIN.name() + ","));

		query.where(QUser.user.id.ne(id).and(bb));
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
	public boolean unlock(Long userId) {
		User user = userRepository.findOne(userId);
		if (user != null) {
			user.setLockedOutUntil(null);
			user.setFailedLogins(null);
			userRepository.save(user);
			return true;
		}		
		return false;
	}
	
	@ExtDirectMethod
	@Transactional(readOnly = true)
	@PreAuthorize("isAuthenticated()")
	public UserSettings readSettings(
			@AuthenticationPrincipal JpaUserDetails jpaUserDetails) {
		return new UserSettings(userRepository.findOne(jpaUserDetails.getUserDbId()));
	}

	@ExtDirectMethod
	@PreAuthorize("isAuthenticated()")
	public List<ValidationMessages> updateSettings(UserSettings modifiedUserSettings,
			@AuthenticationPrincipal JpaUserDetails jpaUserDetails, Locale locale) {

		List<ValidationMessages> validations = ValidationUtil.validateEntity(validator,
				modifiedUserSettings);
		User user = userRepository.findOne(jpaUserDetails.getUserDbId());

		if (validations.isEmpty()) {
			user.setLastName(modifiedUserSettings.getLastName());
			user.setFirstName(modifiedUserSettings.getFirstName());
			user.setEmail(modifiedUserSettings.getEmail());
			user.setLocale(modifiedUserSettings.getLocale());
		}

		if (emailTaken(user.getId(), modifiedUserSettings.getEmail())) {
			ValidationMessages validationError = new ValidationMessages();
			validationError.setField("email");
			validationError.setMessage(messageSource.getMessage(
					"user_emailtaken", null, locale));
			validations.add(validationError);
		}

		if (StringUtils.hasText(modifiedUserSettings.getNewPassword())) {
			if (passwordEncoder.matches(modifiedUserSettings.getCurrentPassword(),
					user.getPasswordHash())) {
				if (modifiedUserSettings.getNewPassword().equals(
						modifiedUserSettings.getNewPasswordRetype())) {
					user.setPasswordHash(passwordEncoder.encode(modifiedUserSettings
							.getNewPassword()));
				}
				else {
					for (String field : new String[] { "newPassword", "newPasswordRetype" }) {
						ValidationMessages error = new ValidationMessages();
						error.setField(field);
						error.setMessage(messageSource.getMessage(
								"user_pwdonotmatch", null, locale));
						validations.add(error);
					}
				}
			}
			else {
				ValidationMessages error = new ValidationMessages();
				error.setField("currentPassword");
				error.setMessage(messageSource.getMessage(
						"user_wrongpassword", null, locale));
				validations.add(error);
			}
		}

		if (validations.isEmpty()) {
			userRepository.save(user);
		}

		return validations;
	}

}
