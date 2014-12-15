package ch.rasc.eds.starter.dto;

import javax.persistence.Transient;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import ch.rasc.eds.starter.entity.User;

public class UserSettings {
	@NotBlank(message = "{fieldrequired}")
	public String firstName;

	@NotBlank(message = "{fieldrequired}")
	public String lastName;

	@NotBlank(message = "{fieldrequired}")
	public String locale;

	@Email(message = "{invalidemail}")
	@NotBlank(message = "{fieldrequired}")
	public String email;

	@Transient
	private boolean passwordReset;

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

	public boolean isPasswordReset() {
		return passwordReset;
	}

	public void setPasswordReset(boolean passwordReset) {
		this.passwordReset = passwordReset;
	}

}
