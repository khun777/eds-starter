package ch.rasc.eds.starter.entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.validation.constraints.Size;

import ch.rasc.edsutil.entity.AbstractPersistable;
import ch.rasc.edsutil.jackson.ISO8601LocalDateTimeSerializer;
import ch.rasc.extclassgenerator.Model;
import ch.rasc.extclassgenerator.ModelField;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity
@Model(value = "Starter.model.AccessLog", readMethod = "accessLogService.read",
		paging = true)
public class AccessLog extends AbstractPersistable {

	@Size(max = 100)
	@JsonIgnore
	private String sessionId;

	@Size(max = 255)
	private String email;

	@ModelField(dateFormat = "c")
	@JsonSerialize(using = ISO8601LocalDateTimeSerializer.class)
	private LocalDateTime loginTimestamp;

	@JsonIgnore
	private String userAgent;

	@Size(max = 20)
	private String userAgentName;

	@Size(max = 10)
	private String userAgentVersion;

	@Size(max = 20)
	private String operatingSystem;

	@Size(max = 45)
	private String ipAddress;

	@Size(max = 255)
	private String location;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public LocalDateTime getLoginTimestamp() {
		return loginTimestamp;
	}

	public void setLoginTimestamp(LocalDateTime loginTimestamp) {
		this.loginTimestamp = loginTimestamp;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public String getUserAgentName() {
		return userAgentName;
	}

	public void setUserAgentName(String userAgentName) {
		this.userAgentName = userAgentName;
	}

	public String getUserAgentVersion() {
		return userAgentVersion;
	}

	public void setUserAgentVersion(String userAgentVersion) {
		this.userAgentVersion = userAgentVersion;
	}

	public String getOperatingSystem() {
		return operatingSystem;
	}

	public void setOperatingSystem(String operatingSystem) {
		this.operatingSystem = operatingSystem;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

}
