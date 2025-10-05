package pencil_utensil.organaut.network.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;

import pencil_utensil.organaut.network.security.filter.JwtFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final JwtFilter jwtFilter;

	SecurityConfig(JwtFilter jwtFilter) {
		this.jwtFilter = jwtFilter;
	}

	@Bean
	@Order(1)
	SecurityFilterChain publicChain(HttpSecurity http) throws Exception {
		return commonConfig(http)
				.securityMatcher(
						RegexRequestMatcher.regexMatcher("^/api/organizations(/[1-9]\\d*|/addresses|/coordinates)?$"))
				.authorizeHttpRequests(authorize -> authorize
						.anyRequest().permitAll())
				.build();
	}

	@Bean
	@Order(2)
	SecurityFilterChain secureChain(HttpSecurity http) throws Exception {
		return commonConfig(http)
				.securityMatcher("/api/organizations/**")
				.authorizeHttpRequests(authorize -> authorize
						.anyRequest().authenticated())
				.addFilterBefore(jwtFilter, AuthorizationFilter.class)
				.build();
	}

	private HttpSecurity commonConfig(HttpSecurity http) throws Exception {
		return http
				.csrf((csrf) -> csrf.disable()) // Do not need it because this attacks has to do with session cookies
				.formLogin(AbstractHttpConfigurer::disable)
				.httpBasic(AbstractHttpConfigurer::disable)
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
	}
}
