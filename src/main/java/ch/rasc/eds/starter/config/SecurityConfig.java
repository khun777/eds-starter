package ch.rasc.eds.starter.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter.XFrameOptionsMode;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
class SecurityConfig {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public CachableWebSecurityConfigurationAdapter cachableWebSecurityConfigurationAdapter() {
		return new CachableWebSecurityConfigurationAdapter();
	}

	@Bean
	public DefaultWebSecurityConfigurerAdapter defaultWebSecurityConfigurerAdapter() {
		return new DefaultWebSecurityConfigurerAdapter();
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth,
			UserDetailsService userDetailsService, PasswordEncoder passwordEncoder)
			throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
	}

	@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER - 1)
	private static class CachableWebSecurityConfigurationAdapter extends
			WebSecurityConfigurerAdapter {
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			// @formatter:off
			http
			  .antMatcher("/**/*.js")
			    .headers()
			      .disable()
			    .authorizeRequests()
			      .antMatchers("/login*.js", "/i18n*.js", "/passwordreset*.js").permitAll()
				  .anyRequest().authenticated()
				  .and()
				.csrf()
				  .disable();
			// @formatter:on
		}
	}

	@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
	public static class DefaultWebSecurityConfigurerAdapter extends
			WebSecurityConfigurerAdapter {
		@Autowired
		private DataSource dataSource;

		@Override
		public void configure(WebSecurity builder) throws Exception {
			builder.ignoring().antMatchers("/resources/**", "/app*.css");
		}

		@Override
		protected void configure(HttpSecurity http) throws Exception {

			JdbcTokenRepositoryImpl tokenRepo = new JdbcTokenRepositoryImpl();
			tokenRepo.setDataSource(dataSource);
			tokenRepo.setCreateTableOnStartup(false);
			tokenRepo.afterPropertiesSet();

			// @formatter:off
			http
			  .headers()
			    .contentTypeOptions()
				.xssProtection()
				.cacheControl()
				.httpStrictTransportSecurity()
				.addHeaderWriter(new XFrameOptionsHeaderWriter(XFrameOptionsMode.SAMEORIGIN))
			    .and()
			  .authorizeRequests()
			    .antMatchers("/passwordresetEmail", "/passwordreset.html", "/passwordreset.action").permitAll()
			    .anyRequest().authenticated()
			    .and()
			  .formLogin()
			    .loginPage("/login.html")
			    .defaultSuccessUrl("/index.html", true)
			    .permitAll()
			    .and()
			  .logout()
			    .deleteCookies("JSESSIONID")
			    .permitAll()
			    .and()
			  .rememberMe()
			    .tokenRepository(tokenRepo)
			    .and()
			  .csrf()
			    .disable();
			// @formatter:on

		}
	}

}