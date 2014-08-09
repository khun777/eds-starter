package ch.rasc.eds.starter.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ch.rasc.eds.starter.entity.AccessLog;

public interface AccessLogRepository extends JpaRepository<AccessLog, Long> {
	// nothing here
}
