package ch.rasc.eds.starter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Locale;

import javax.servlet.Filter;

import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.LocaleResolver;

import ch.ralscha.extdirectspring.ExtDirectSpring;
import ch.rasc.eds.starter.config.AppLocaleResolver;
import ch.rasc.eds.starter.entity.User;
import ch.rasc.eds.starter.web.MdcFilter;
import ch.rasc.edsutil.optimizer.WebResourceProcessor;

@Configuration
@ComponentScan(basePackageClasses = { ExtDirectSpring.class, Application.class })
@EnableAutoConfiguration
@EnableScheduling
@PropertySource("classpath:/version.properties")
@EntityScan(basePackageClasses = { ch.rasc.edsutil.entity.AbstractPersistable.class,
		User.class })
public class Application extends SpringBootServletInitializer {

	// -Dspring.profiles.active=development
	public static void main(String[] args) throws Exception {
		SpringApplication.run(Application.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Application.class);
	}

	@Bean
	public Filter characterEncodingFilter() {
		CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
		characterEncodingFilter.setEncoding(StandardCharsets.UTF_8.name());
		characterEncodingFilter.setForceEncoding(false);
		return characterEncodingFilter;
	}

	@Bean
	public Filter mdcFilter() {
		return new MdcFilter();
	}

	@Bean
	public ch.ralscha.extdirectspring.controller.Configuration configuration() {
		ch.ralscha.extdirectspring.controller.Configuration config = new ch.ralscha.extdirectspring.controller.Configuration();
		config.setExceptionToMessage(Collections.singletonMap(
				AccessDeniedException.class, "accessdenied"));
		return config;
	}

	@Bean
	public LocaleResolver localeResolver() {
		AppLocaleResolver resolver = new AppLocaleResolver();
		resolver.setDefaultLocale(Locale.ENGLISH);
		return resolver;
	}

	@Bean
	public ServletContextInitializer servletContextInitializer(
			final Environment environment) {
		return servletContext -> {
			try {
				boolean isDefaultProfileActive = environment.acceptsProfiles("default");
				WebResourceProcessor processor = new WebResourceProcessor(servletContext,
						isDefaultProfileActive);
				processor.process();
			}
			catch (IOException e) {
				LoggerFactory.getLogger(Application.class).error("read index.html", e);
			}
		};
	}

	@Bean
	@Profile("compression")
	public EmbeddedServletContainerCustomizer servletContainerCustomizer() {
		return servletContainer -> ((TomcatEmbeddedServletContainerFactory) servletContainer)
				.addConnectorCustomizers(connector -> {
					AbstractHttp11Protocol<?> httpProtocol = (AbstractHttp11Protocol<?>) connector
							.getProtocolHandler();
					httpProtocol.setCompression("on");
					httpProtocol.setCompressionMinSize(512);
					String mimeTypes = httpProtocol.getCompressableMimeTypes();
					String additionalMimeTypes = mimeTypes + ","
							+ MediaType.APPLICATION_JSON_VALUE + ","
							+ "application/javascript,text/css";

					httpProtocol.setCompressableMimeTypes(additionalMimeTypes);
				});
	}
}