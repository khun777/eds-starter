package ch.rasc.eds.starter.web;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import ch.rasc.eds.starter.config.security.JpaUserDetails;
import ch.rasc.eds.starter.entity.QUser;
import ch.rasc.eds.starter.entity.User;
import ch.rasc.eds.starter.service.MailService;

import com.mysema.query.jpa.impl.JPAQuery;

@Controller
public class PasswordResetController {

	private final MailService mailService;

	private final PasswordEncoder passwordEncoder;

	private final EntityManager entityManager;

	@Autowired
	public PasswordResetController(EntityManager entityManager, MailService mailService,
			PasswordEncoder passwordEncoder) {
		this.entityManager = entityManager;
		this.mailService = mailService;
		this.passwordEncoder = passwordEncoder;
	}

	@RequestMapping(value = "passwordreset.action", method = RequestMethod.POST)
	@Transactional
	public void passwordreset(HttpServletRequest request, HttpServletResponse response,
			String newPassword, String newPasswordRetype, String token)
			throws IOException {

		if (StringUtils.hasText(token) && StringUtils.hasText(newPassword)
				&& StringUtils.hasText(newPasswordRetype)
				&& newPassword.equals(newPasswordRetype)) {
			String decodedToken = new String(Base64.getUrlDecoder().decode(token));
			User user = new JPAQuery(entityManager).from(QUser.user)
					.where(QUser.user.passwordResetToken.eq(decodedToken))
					.singleResult(QUser.user);
			if (user != null && user.getPasswordResetTokenValidUntil() != null) {
				if (user.getPasswordResetTokenValidUntil().isAfter(LocalDateTime.now())) {
					user.setPasswordHash(passwordEncoder.encode(newPassword));

					JpaUserDetails principal = new JpaUserDetails(user);
					UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
							principal, null, principal.getAuthorities());
					SecurityContextHolder.getContext().setAuthentication(authToken);
				}
				user.setPasswordResetToken(null);
				user.setPasswordResetTokenValidUntil(null);
				entityManager.merge(user);
			}
		}

		response.sendRedirect(request.getContextPath());
	}

	@RequestMapping(value = { "/passwordresetEmail" }, method = RequestMethod.POST)
	@ResponseBody
	@Transactional
	public void passwordresetEmail(String email) {
		List<User> users = new JPAQuery(entityManager).from(QUser.user)
				.where(QUser.user.email.eq(email).or(QUser.user.email.eq(email)))
				.list(QUser.user);

		User user = null;
		if (users.size() > 1) {
			user = new JPAQuery(entityManager).from(QUser.user)
					.where(QUser.user.email.eq(email)).singleResult(QUser.user);
		}
		else if (users.size() == 1) {
			user = users.iterator().next();
		}

		if (user != null) {
			String token = UUID.randomUUID().toString();
			mailService.sendPasswortResetEmail(user, token);

			user.setPasswordResetTokenValidUntil(LocalDateTime.now().plusHours(4));
			user.setPasswordResetToken(token);
		}

	}

}
