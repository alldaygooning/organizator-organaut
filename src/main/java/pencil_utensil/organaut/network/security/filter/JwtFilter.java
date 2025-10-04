package pencil_utensil.organaut.network.security.filter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.reactive.function.client.WebClient;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import reactor.core.publisher.Mono;

@Component
public class JwtFilter extends OncePerRequestFilter {

	private static final Logger LOGGER = LoggerFactory.getLogger(JwtFilter.class);

	private final WebClient webClient;
	private final String apiKey;
	private final String url;

	public JwtFilter(
			@Value("${lobby_boy.api.key}") String apiKey,
			@Value("${lobby_boy.url}") String url) {
		this.apiKey = apiKey;
		this.url = url;

		this.webClient = WebClient.builder()
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.build();
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		Optional<String> opt = extractJwtFromCookie(request);
		if (opt.isEmpty()) {
			sendError(response, HttpStatus.UNAUTHORIZED, "Missing authentication token");
			return;
		}


		try {
			Verdict verdict = this.webClient.post()
					.uri(this.url + "/api/jwt/validate")
					.header("X-API-KEY", apiKey)
					.bodyValue(Map.of("token", opt.get()))
					.retrieve()
					.onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
							clientResponse -> Mono.error(new RuntimeException("Authentication service error")))
					.bodyToMono(Verdict.class)
					.timeout(Duration.ofSeconds(10))
					.block();
			if (verdict == null || !verdict.valid) {
				sendError(response, HttpStatus.UNAUTHORIZED, "Invalid authentication token");
				return;
			}

			JwtAuthentication authentication = new JwtAuthentication(verdict);
			SecurityContextHolder.getContext().setAuthentication(authentication);
		} catch (Exception e) {
			LOGGER.error("Authentication service error", e);
			sendError(response, HttpStatus.INTERNAL_SERVER_ERROR, "Authentication service unavailable");
			return;
		}
		filterChain.doFilter(request, response);
	}

	private Optional<String> extractJwtFromCookie(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("auth_token".equals(cookie.getName())) {
					return Optional.of(cookie.getValue());
				}
			}
		}
		return Optional.empty();
	}

	private void sendError(HttpServletResponse response, HttpStatus status, String message) throws IOException {
		response.setStatus(status.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.getWriter().write("{\"error\": \"" + message + "\"}");
	}

	public static class Verdict {
		public boolean valid;
		public String name;
		public Integer id;
	}

	public static class JwtAuthentication extends AbstractAuthenticationToken {
		private static final long serialVersionUID = 1L;
		private final String name;
		private final Integer id;

		public JwtAuthentication(Verdict verdict) {
			super(null);
			this.name = verdict.name;
			this.id = verdict.id;
			setAuthenticated(verdict.valid);
		}

		@Override
		public Object getCredentials() { return null; }

		@Override
		public Object getPrincipal() { return Map.of("id", id, "name", name); }

		public String getName() { return name; }

		public Integer getId() { return id; }

		@Override
		public boolean isAuthenticated() { return true; }
	}
}
