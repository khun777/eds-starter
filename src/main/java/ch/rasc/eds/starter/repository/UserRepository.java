package ch.rasc.eds.starter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import ch.rasc.eds.starter.entity.User;

public interface UserRepository extends JpaRepository<User, Long>,
		QueryDslPredicateExecutor<User> {
	User findByEmail(String email);
}
