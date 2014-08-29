package ch.rasc.eds.starter.dto;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import ch.rasc.eds.starter.entity.User;

public class UserSettings {
	@NotEmpty(message = "{fieldrequired}")
	public String firstName;

	@NotEmpty(message = "{fieldrequired}")
	public String lastName;

	@NotEmpty(message = "{fieldrequired}")
	public String locale;

	@Email(message = "{invalidemail}")
	@NotEmpty(message = "{fieldrequired}")
	public String email;

	public String currentPassword;
	public String newPassword;
	public String newPasswordRetype;

	public UserSettings() {
		// default constructor
	}

	public UserSettings(User user) {
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.locale = user.getLocale();
		this.email = user.getEmail();
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCurrentPassword() {
		return currentPassword;
	}

	public void setCurrentPassword(String currentPassword) {
		this.currentPassword = currentPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getNewPasswordRetype() {
		return newPasswordRetype;
	}

	public void setNewPasswordRetype(String newPasswordRetype) {
		this.newPasswordRetype = newPasswordRetype;
	}

}
