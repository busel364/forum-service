package telran.java47.security.filter;

import java.io.IOException;
import java.security.Principal;
import java.util.Base64;
import java.util.Set;
import java.util.stream.Collectors;

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
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import telran.java47.accounting.dao.UserAccountRepository;
import telran.java47.accounting.model.UserAccount;
import telran.java47.security.context.SecurityContext;
import telran.java47.security.enums.RolesEnum;
import telran.java47.security.model.User;

@Component
@Order(10)
@RequiredArgsConstructor
public class AuthenticationFilter implements Filter {

	final UserAccountRepository userAccountRepository;
	final SecurityContext securityContext;

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		if (checkEndPoint(request.getMethod(), request.getServletPath())) {
			String sessionId = request.getSession().getId();
			User user = securityContext.getUserBySessionId(sessionId);
			if (user == null) {
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
				user = new User(userAccount.getLogin(), userAccount.getRoles());
				securityContext.addUserSession(user, sessionId);
			}

			request = new WrappedRequest(request, user.getName(), user.getRoles());
		}

		chain.doFilter(request, response);
	}

	private boolean checkEndPoint(String method, String path) {
		return !((HttpMethod.POST.matches(method) && path.matches("/account/register/?"))
				|| path.matches("/forum/posts/\\w+(/\\w+)?/?"));
	}

	private String[] getCredentials(String token) {
		token = token.substring(6);
		String decoded = new String(Base64.getDecoder().decode(token));
		return decoded.split(":");
	}

	private static class WrappedRequest extends HttpServletRequestWrapper {
		String login;
		Set<String> roles;

		public WrappedRequest(HttpServletRequest request, String login, Set<RolesEnum> roles) {
			super(request);
			this.login = login;
			this.roles = roles.stream().map(v->v.getTitle()).collect(Collectors.toSet());
		}

		@Override
		public Principal getUserPrincipal() {
			return new User(login, roles);
		}

	}
}
