package ch.rasc.eds.starter.config.security;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import ch.rasc.eds.starter.entity.QUser;
import ch.rasc.eds.starter.entity.User;

import com.mysema.query.jpa.impl.JPAQuery;

@Component
public class JpaUserDetailsService implements UserDetailsService {

	private final EntityManager entityManager;

	@Autowired
	public JpaUserDetailsService(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = new JPAQuery(entityManager).from(QUser.user)
				.where(QUser.user.email.eq(email).and(QUser.user.deleted.isFalse()))
				.singleResult(QUser.user);

		if (user != null) {
			return new JpaUserDetails(user);
		}

		throw new UsernameNotFoundException(email);
	}

}
