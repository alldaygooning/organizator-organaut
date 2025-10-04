package pencil_utensil.organaut.network.security.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class FilterConfig {
	@Bean
	FilterRegistrationBean<JwtFilter> jwtFilterRegistration(JwtFilter jwtFilter) {
		FilterRegistrationBean<JwtFilter> reg = new FilterRegistrationBean<>(jwtFilter);
		reg.setEnabled(false);
		return reg;
	}
}
