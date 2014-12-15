package ch.rasc.eds.starter.service;

import java.time.LocalDateTime;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.sf.uadetector.ReadableUserAgent;
import net.sf.uadetector.UserAgentStringParser;
import net.sf.uadetector.service.UADetectorServiceFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.rasc.eds.starter.config.security.JpaUserDetails;
import ch.rasc.eds.starter.entity.AccessLog;
import ch.rasc.eds.starter.entity.User;

@Service
public class SecurityService {
	private final static UserAgentStringParser UAPARSER = UADetectorServiceFactory
			.getResourceModuleParser();

	private final EntityManager entityManager;

	private final GeoIPCityService geoIpCityService;

	@Autowired
	public SecurityService(EntityManager entityManager,
			GeoIPCityService geoIpCityService) {
		this.entityManager = entityManager;
		this.geoIpCityService = geoIpCityService;
	}

	@ExtDirectMethod
	@PreAuthorize("isAuthenticated()")
	@Transactional
	public User getLoggedOnUser(HttpServletRequest request, HttpSession session,
			@AuthenticationPrincipal JpaUserDetails jpaUserDetails) {

		if (jpaUserDetails != null) {
			User user = jpaUserDetails.getUser(entityManager);
			if (jpaUserDetails.hasRole("ADMIN")) {
				user.setAutoOpenView("Starter.view.accesslog.TabPanel");
			}
			else if (jpaUserDetails.hasRole("USER")) {
				user.setAutoOpenView("Starter.view.dummy.View");
			}
			insertAccessLog(request, session, user);
			return user;
		}

		return null;
	}

	private void insertAccessLog(HttpServletRequest request, HttpSession session,
			User user) {

		AccessLog accessLog = new AccessLog();
		accessLog.setEmail(user.getEmail());
		accessLog.setSessionId(session.getId());
		accessLog.setLoginTimestamp(LocalDateTime.now());

		String ipAddress = request.getRemoteAddr();
		accessLog.setIpAddress(ipAddress);
		accessLog.setLocation(geoIpCityService.lookupCity(ipAddress));

		String ua = request.getHeader("User-Agent");
		if (StringUtils.hasText(ua)) {
			accessLog.setUserAgent(ua);
			ReadableUserAgent agent = UAPARSER.parse(ua);
			accessLog.setUserAgentName(agent.getName());
			accessLog.setUserAgentVersion(agent.getVersionNumber().getMajor());
			accessLog.setOperatingSystem(agent.getOperatingSystem().getFamilyName());
		}

		entityManager.persist(accessLog);
	}

	@ExtDirectMethod
	@PreAuthorize("hasRole('ADMIN')")
	@Transactional(readOnly = true)
	public boolean switchUser(Long userId) {
		User switchToUser = entityManager.find(User.class, userId);
		if (switchToUser != null) {

			JpaUserDetails principal = new JpaUserDetails(switchToUser);
			UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
					principal, null, principal.getAuthorities());

			SecurityContextHolder.getContext().setAuthentication(token);

			return true;
		}

		return false;
	}

}
