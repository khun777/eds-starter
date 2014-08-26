package ch.rasc.eds.starter.web;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HtmlController {

	private final String indexHtml;

	private final String appJs;

	private final String loginJs;

	private final Environment environment;

	@Autowired
	public HtmlController(Environment environment, ServletContext servletContext)
			throws IOException {
		this.environment = environment;

		ClassPathResource cp = new ClassPathResource("loader.css");
		String loadercss;
		try (InputStream is = cp.getInputStream()) {
			loadercss = StreamUtils.copyToString(is, StandardCharsets.UTF_8);
		}

		cp = new ClassPathResource("index.template");
		String htmlTemplate;
		try (InputStream is = cp.getInputStream()) {
			htmlTemplate = StreamUtils.copyToString(is, StandardCharsets.UTF_8);
		}

		indexHtml = htmlTemplate.replace("application.loader_css", loadercss).replace(
				"application.app_css", (String) servletContext.getAttribute("app_css"));

		appJs = (String) servletContext.getAttribute("app_js");
		loginJs = (String) servletContext.getAttribute("login_js");
	}

	@RequestMapping(value = { "/", "/index.html" }, produces = MediaType.TEXT_HTML_VALUE)
	@ResponseBody
	public String index(HttpServletResponse response, Locale locale) {
		response.setContentType("text/html; charset=utf-8");
		return indexHtml + createI18nScript(environment, locale) + appJs
				+ createExtJSLocale(environment, locale) + "</body></html>";
	}

	@RequestMapping(value = { "/login.html" }, produces = MediaType.TEXT_HTML_VALUE)
	@ResponseBody
	public String login(HttpServletResponse response, Locale locale) {
		response.setContentType("text/html; charset=utf-8");
		return indexHtml + createI18nScript(environment, locale) + loginJs
				+ createExtJSLocale(environment, locale) + "</body></html>";
	}

	private static String createExtJSLocale(Environment environment, Locale locale) {
		String extjsLocale = "";
		if (locale != null && locale.getLanguage().toLowerCase().equals("de")) {
			String extjsVersion = environment.getProperty("extjs.version");
			extjsLocale = "<script src=\"resources/ext/" + extjsVersion
					+ "/locale/ext-lang-de.js\"></script>";
		}
		return extjsLocale;
	}

	private static String createI18nScript(Environment environment, Locale locale) {
		String i18nScript;
		if (environment.acceptsProfiles("development")) {
			i18nScript = "<script src=\"i18n.js\"></script>";
		}
		else {
			i18nScript = "<script src=\"i18n-" + locale + "_"
					+ environment.getProperty("application.version") + ".js\"></script>";
		}
		return i18nScript;
	}

}
