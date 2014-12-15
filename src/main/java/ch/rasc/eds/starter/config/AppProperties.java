package ch.rasc.eds.starter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "app")
@Component
public class AppProperties {
	private String url;

	private String defaultEmailSender;
	
	private String geoIp2CityDatabaseFile;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDefaultEmailSender() {
		return defaultEmailSender;
	}

	public void setDefaultEmailSender(String defaultEmailSender) {
		this.defaultEmailSender = defaultEmailSender;
	}

	public String getGeoIp2CityDatabaseFile() {
		return geoIp2CityDatabaseFile;
	}

	public void setGeoIp2CityDatabaseFile(String geoIp2CityDatabaseFile) {
		this.geoIp2CityDatabaseFile = geoIp2CityDatabaseFile;
	}

}
