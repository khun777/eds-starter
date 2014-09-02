package ch.rasc.eds.starter.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ch.rasc.eds.starter.entity.Configuration;
import ch.rasc.eds.starter.entity.ConfigurationKey;

public interface ConfigurationRepository extends JpaRepository<Configuration, Long> {

	Configuration findByConfKey(ConfigurationKey key);

}
