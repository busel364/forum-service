package telran.java47.security.filter;

import java.io.IOException;
import java.security.Principal;
import java.util.Base64;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import telran.java47.accounting.dao.UserAccountRepository;
import telran.java47.accounting.model.UserAccount;

@Component
@Order(10)
@RequiredArgsConstructor
public class AuthenticationFilter implements Filter {

	final UserAccountRepository userAccountRepository;

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		if (checkEndPoint(request.getMethod(), request.getServletPath())) {
			String[] credential;
			try {
				credential = getCredentials(request.getHeader("Authorization"));
			} catch (Exception e) {
				response.sendError(401, "Token is not valid");
				return;
			}
			UserAccount userAccount = userAccountRepository.findById(credential[0]).orElseThrow(null);
			if (userAccount == null || !BCrypt.checkpw(credential[1], userAccount.getPassword())) {
				response.sendError(401, "Login or password is not valid");
				return;
			}
			request = new WrappedRequest(request, credential[0]);
		}

		chain.doFilter(request, response);
	}

	private boolean checkEndPoint(String method, String path) {
		String pattern = "/forum/posts/.*";
		if (("POST".equalsIgnoreCase(method) && path.matches(pattern))
				|| ("GET".equalsIgnoreCase(method) && path.matches(pattern))) {
			return false;
		}
		return !("POST".equalsIgnoreCase(method) && path.matches("/account/register/?"));
	}

	private String[] getCredentials(String token) {
		token = token.substring(6);
		String decoded = new String(Base64.getDecoder().decode(token));
		return decoded.split(":");
	}

	private static class WrappedRequest extends HttpServletRequestWrapper {
		String login;

		public WrappedRequest(HttpServletRequest request, String login) {
			super(request);
			this.login = login;
		}

		@Override
		public Principal getUserPrincipal() {
			return () -> login;
		}

	}
}
