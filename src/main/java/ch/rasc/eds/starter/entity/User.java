package ch.rasc.eds.starter.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import ch.rasc.edsutil.entity.AbstractPersistable;
import ch.rasc.edsutil.jackson.ISO8601LocalDateTimeSerializer;
import ch.rasc.extclassgenerator.Model;
import ch.rasc.extclassgenerator.ModelField;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity
@Table(name = "AppUser")
@Model(value = "Starter.model.User", readMethod = "userService.read",
		createMethod = "userService.update", updateMethod = "userService.update",
		destroyMethod = "userService.destroy", paging = true, identifier = "negative")
public class User extends AbstractPersistable {

	@NotBlank(message = "{fieldrequired}")
	@Size(max = 255)
	private String lastName;

	@NotBlank(message = "{fieldrequired}")
	@Size(max = 255)
	private String firstName;

	@Email(message = "{invalidemail}")
	@NotBlank(message = "{fieldrequired}")
	@Size(max = 255)
	@Column(unique = true)
	private String email;

	private String role;

	@Size(max = 255)
	@JsonIgnore
	private String passwordHash;

	@Transient
	private String newPassword;

	@Transient
	private String newPasswordRetype;

	@NotBlank(message = "{fieldrequired}")
	@Size(max = 8)
	private String locale;

	private boolean enabled;

	@Transient
	private boolean passwordReset;

	@ModelField(persist = false)
	private Integer failedLogins;

	@ModelField(dateFormat = "c", persist = false)
	@JsonSerialize(using = ISO8601LocalDateTimeSerializer.class)
	private LocalDateTime lockedOutUntil;

	@ModelField(persist = false)
	@Transient
	private String lastLogin;

	@Size(max = 36)
	@JsonIgnore
	private String passwordResetToken;

	@JsonIgnore
	private LocalDateTime passwordResetTokenValidUntil;

	@JsonIgnore
	@Column(name = "is_deleted")
	private boolean deleted;

	@Transient
	@ModelField(persist = false)
	private String autoOpenView;

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public boolean isPasswordReset() {
		return passwordReset;
	}

	public void setPasswordReset(boolean passwordReset) {
		this.passwordReset = passwordReset;
	}

	public Integer getFailedLogins() {
		return failedLogins;
	}

	public void setFailedLogins(Integer failedLogins) {
		this.failedLogins = failedLogins;
	}

	public LocalDateTime getLockedOutUntil() {
		return lockedOutUntil;
	}

	public void setLockedOutUntil(LocalDateTime lockedOutUntil) {
		this.lockedOutUntil = lockedOutUntil;
	}

	public String getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(String lastLogin) {
		this.lastLogin = lastLogin;
	}

	public String getPasswordResetToken() {
		return passwordResetToken;
	}

	public void setPasswordResetToken(String passwordResetToken) {
		this.passwordResetToken = passwordResetToken;
	}

	public LocalDateTime getPasswordResetTokenValidUntil() {
		return passwordResetTokenValidUntil;
	}

	public void setPasswordResetTokenValidUntil(LocalDateTime passwordResetTokenValidUntil) {
		this.passwordResetTokenValidUntil = passwordResetTokenValidUntil;
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

	public String getAutoOpenView() {
		return autoOpenView;
	}

	public void setAutoOpenView(String autoOpenView) {
		this.autoOpenView = autoOpenView;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
}
