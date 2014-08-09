package ch.rasc.eds.starter.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ch.rasc.eds.starter.entity.Configuration;

public interface ConfigurationRepository extends JpaRepository<Configuration, Long> {
	// nothing here
}
