package ch.rasc.eds.starter.service;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.rasc.eds.starter.config.AppProperties;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.AddressNotFoundException;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;

@Service
public class GeoIPCityService {

	private final static Logger logger = LoggerFactory.getLogger(GeoIPCityService.class);

	private DatabaseReader reader = null;

	@Autowired
	public GeoIPCityService(AppProperties appProperties) {
		String databaseFile = appProperties.getGeoIp2CityDatabaseFile();
		if (databaseFile != null) {
			Path database = Paths.get(databaseFile);
			if (Files.exists(database)) {
				try {
					reader = new DatabaseReader.Builder(database.toFile()).build();
				}
				catch (IOException e) {
					logger.error("GeoIPCityService init", e);
				}
			}
		}
	}

	public String lookupCity(String ip) {
		if (reader != null) {
			CityResponse response;
			try {
				try {
					response = reader.city(InetAddress.getByName(ip));
				}
				catch (AddressNotFoundException e) {
					return null;
				}
				if (response != null) {
					String location = response.getCity().getName();
					if (location == null) {
						location = response.getCountry().getName();
					}
					return location;
				}
			}
			catch (IOException | GeoIp2Exception e) {
				logger.error("lookupCity", e);
			}
		}

		return null;
	}

}
