package ch.rasc.eds.starter.config.security;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import ch.rasc.eds.starter.entity.User;
import ch.rasc.eds.starter.repository.UserRepository;

@Component
public class UserAuthenticationSuccessfulHandler implements
		ApplicationListener<InteractiveAuthenticationSuccessEvent> {

	private final UserRepository userRepository;

	@Autowired
	public UserAuthenticationSuccessfulHandler(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public void onApplicationEvent(InteractiveAuthenticationSuccessEvent event) {
		Object principal = event.getAuthentication().getPrincipal();
		if (principal instanceof JpaUserDetails) {
			User user = userRepository
					.findOne(((JpaUserDetails) principal).getUserDbId());
			user.setLockedOutUntil(null);
			user.setFailedLogins(null);
			user.setLastLogin(LocalDateTime.now());
			userRepository.save(user);
		}
	}
}