package ch.rasc.eds.starter.config.security;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import ch.rasc.eds.starter.entity.User;

@Component
public class UserAuthenticationSuccessfulHandler implements
		ApplicationListener<InteractiveAuthenticationSuccessEvent> {

	private final EntityManager entityManager;

	@Autowired
	public UserAuthenticationSuccessfulHandler(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	@Transactional
	public void onApplicationEvent(InteractiveAuthenticationSuccessEvent event) {
		Object principal = event.getAuthentication().getPrincipal();
		if (principal instanceof JpaUserDetails) {
			User user = entityManager.find(User.class,
					((JpaUserDetails) principal).getUserDbId());
			user.setLockedOutUntil(null);
			user.setFailedLogins(null);
			entityManager.merge(user);
		}
	}
}