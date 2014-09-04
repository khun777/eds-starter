package ch.rasc.eds.starter.web;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import ch.rasc.eds.starter.config.AppProperties;
import ch.rasc.eds.starter.config.security.JpaUserDetails;
import ch.rasc.eds.starter.entity.User;
import ch.rasc.eds.starter.repository.UserRepository;
import ch.rasc.eds.starter.service.MailService;

@Controller
public class PasswordResetController {

	private final MailService mailService;

	private final UserRepository userRepository;

	private final PasswordEncoder passwordEncoder;

	private final AppProperties appProperties;

	@Autowired
	public PasswordResetController(UserRepository userRepository,
			MailService mailService, PasswordEncoder passwordEncoder,
			AppProperties appProperties) {
		this.userRepository = userRepository;
		this.mailService = mailService;
		this.passwordEncoder = passwordEncoder;
		this.appProperties = appProperties;
	}

	@RequestMapping(value = "passwordreset.action", method = RequestMethod.POST)
	public void passwordreset(HttpServletRequest request, HttpServletResponse response,
			String newPassword, String newPasswordRetype, String token)
			throws IOException {

		if (StringUtils.hasText(token) && StringUtils.hasText(newPassword)
				&& StringUtils.hasText(newPasswordRetype)
				&& newPassword.equals(newPasswordRetype)) {
			String decodedToken = new String(Base64.getUrlDecoder().decode(token));
			User user = userRepository.findByPasswordResetToken(decodedToken);
			if (user != null) {
				if (user.getPasswordResetTokenValidUntil().isAfter(LocalDateTime.now())) {
					user.setPasswordHash(passwordEncoder.encode(newPassword));

					JpaUserDetails principal = new JpaUserDetails(user);
					UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
							principal, null, principal.getAuthorities());
					SecurityContextHolder.getContext().setAuthentication(authToken);
				}
				user.setPasswordResetToken(null);
				user.setPasswordResetTokenValidUntil(null);
				userRepository.save(user);
			}
		}

		response.sendRedirect(request.getContextPath());
	}

	@RequestMapping(value = { "/passwordresetEmail" }, method = RequestMethod.POST)
	@ResponseBody
	public void passwordresetEmail(HttpServletRequest request, String email) {
		User user = userRepository.findByEmail(email);

		if (user != null) {
			String token = UUID.randomUUID().toString();

			String scheme = request.getScheme();
			String serverName = request.getServerName();
			int serverPort = request.getServerPort();
			String contextPath = request.getContextPath();

			String passwordResetUrl;
			if (StringUtils.hasText(appProperties.getUrl())) {
				passwordResetUrl = appProperties.getUrl();
			}
			else {
				passwordResetUrl = scheme + "://" + serverName
						+ (serverPort != 80 ? ":" + serverPort : "") + contextPath;
			}
			passwordResetUrl += "/passwordreset.html?token="
					+ Base64.getUrlEncoder().encodeToString(token.getBytes());

			mailService.sendSimpleMessage(email, "Starter Password Reset",
					passwordResetUrl);

			user.setPasswordResetTokenValidUntil(LocalDateTime.now().plusHours(4));
			user.setPasswordResetToken(token);
			userRepository.save(user);
		}

	}

}
