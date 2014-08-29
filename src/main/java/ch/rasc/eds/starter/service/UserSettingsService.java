package ch.rasc.eds.starter.service;

import java.util.List;
import java.util.Locale;

import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.rasc.eds.starter.Util;
import ch.rasc.eds.starter.ValidationErrors;
import ch.rasc.eds.starter.config.security.JpaUserDetails;
import ch.rasc.eds.starter.dto.UserSettings;
import ch.rasc.eds.starter.entity.User;
import ch.rasc.eds.starter.repository.UserRepository;

@Service
public class UserSettingsService {

	private final UserRepository userRepository;

	private final MessageSource messageSource;

	private final PasswordEncoder passwordEncoder;

	private final Validator validator;

	@Autowired
	public UserSettingsService(UserRepository userRepository,
			PasswordEncoder passwordEncoder, Validator validator,
			MessageSource messageSource) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.messageSource = messageSource;
		this.validator = validator;
	}

	@ExtDirectMethod
	@Transactional(readOnly = true)
	@PreAuthorize("isAuthenticated()")
	public UserSettings read(@AuthenticationPrincipal JpaUserDetails jpaUserDetails) {
		User user = userRepository.findOne(jpaUserDetails.getUserDbId());
		return new UserSettings(user);
	}

	@ExtDirectMethod
	@PreAuthorize("isAuthenticated()")
	public List<ValidationErrors> updateSettings(UserSettings modifiedUserSettings,
			@AuthenticationPrincipal JpaUserDetails jpaUserDetails, Locale locale) {

		List<ValidationErrors> validations = Util.validateObject(validator,
				modifiedUserSettings);
		User user = userRepository.findOne(jpaUserDetails.getUserDbId());

		if (validations.isEmpty()) {
			user.setLastName(modifiedUserSettings.getLastName());
			user.setFirstName(modifiedUserSettings.getFirstName());
			user.setEmail(modifiedUserSettings.getEmail());
			user.setLocale(modifiedUserSettings.getLocale());
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
						ValidationErrors error = new ValidationErrors();
						error.setField(field);
						error.setMessage(new String[] { messageSource.getMessage(
								"user_pwdonotmatch", null, locale) });
						validations.add(error);
					}
				}
			}
			else {
				ValidationErrors error = new ValidationErrors();
				error.setField("currentPassword");
				error.setMessage(new String[] { messageSource.getMessage(
						"user_wrongpassword", null, locale) });
				validations.add(error);
			}
		}

		if (validations.isEmpty()) {
			userRepository.save(user);
		}

		return validations;
	}

}
