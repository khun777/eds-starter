package ch.rasc.eds.starter.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import ch.rasc.edsutil.entity.AbstractPersistable;
import ch.rasc.extclassgenerator.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "AppUser")
@Model(value = "Starter.model.User", readMethod = "userService.read",
		createMethod = "userService.create", updateMethod = "userService.update",
		destroyMethod = "userService.destroy", paging = true, identifier = "negative")
public class User extends AbstractPersistable {

	@NotEmpty(message = "{fieldrequired}")
	@Size(max = 255)
	private String lastName;

	@NotEmpty(message = "{fieldrequired}")
	@Size(max = 255)
	private String firstName;

	@Email(message = "{invalidemail}")
	@NotEmpty(message = "{fieldrequired}")
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

	@NotEmpty(message = "{fieldrequired}")
	@Size(max = 8)
	private String locale;

	private boolean enabled;

	@JsonIgnore
	private Integer failedLogins;

	@JsonIgnore
	private LocalDateTime lockedOutUntil;

	@JsonIgnore
	private LocalDateTime lastLogin;

	@Size(max = 36)
	@JsonIgnore
	private String passwordResetToken;

	@JsonIgnore
	private LocalDateTime passwordResetTokenValidUntil;

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

	public LocalDateTime getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(LocalDateTime lastLogin) {
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

}
