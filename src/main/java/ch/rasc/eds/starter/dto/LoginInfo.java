package ch.rasc.eds.starter.dto;

public class LoginInfo {
	private String roleName;
	private String login;
	private String autoOpenView;
	private int geschaeftsjahr;
	private String arbeitgeber;

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getAutoOpenView() {
		return autoOpenView;
	}

	public void setAutoOpenView(String autoOpenView) {
		this.autoOpenView = autoOpenView;
	}

	public int getGeschaeftsjahr() {
		return geschaeftsjahr;
	}

	public void setGeschaeftsjahr(int geschaeftsjahr) {
		this.geschaeftsjahr = geschaeftsjahr;
	}

	public String getArbeitgeber() {
		return arbeitgeber;
	}

	public void setArbeitgeber(String arbeitgeber) {
		this.arbeitgeber = arbeitgeber;
	}

}
