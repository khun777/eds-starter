package ch.rasc.eds.starter.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import ch.rasc.edsutil.entity.AbstractPersistable;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "client_state")
public class ClientState extends AbstractPersistable {

	@Column(name = "name")
	private String name;

	@Column(name = "value")
	private String value;

	@Column(name = "user_id")
	@JsonIgnore
	private long userId;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

}
