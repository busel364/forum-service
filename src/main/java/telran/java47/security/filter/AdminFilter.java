package telran.java47.security.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import telran.java47.accounting.dao.UserAccountRepository;
import telran.java47.accounting.dto.exeptions.UserNotFoundExeption;
import telran.java47.accounting.model.UserAccount;

@Component
@Order(30)
@RequiredArgsConstructor
public class AdminFilter implements Filter {

	final UserAccountRepository userAccountRepository;

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		String userName = "";
		String[] list = request.getServletPath().split("/");
		if (checkEndPoint(request.getMethod(), request.getServletPath())) {
			try {
				userName = request.getUserPrincipal().getName();
			} catch (Exception e) {
				throw new UserNotFoundExeption();
			}
			UserAccount userAccount = userAccountRepository.findById(userName).orElseThrow(UserNotFoundExeption::new);
			if (checkOwner(request, userAccount, list[2])) {
				chain.doFilter(request, response);
				return;
			}
			if (!checkAdminAccess(userAccount)) {
				response.sendError(403, "Forbiden");
				return;
			}
		}
		chain.doFilter(request, response);
	}

	private boolean checkOwner(HttpServletRequest request, UserAccount user, String login) {
		return (user.getLogin().equals(login)) && ("DELETE".equalsIgnoreCase(request.getMethod())
				&& request.getServletPath().matches("/account/user/.*"));
	}

	private boolean checkEndPoint(String method, String path) {
		return ("DELETE".equalsIgnoreCase(method) && path.matches("/account/user/.*/role/\\w{5,9}/?"))
				|| ("PUT".equalsIgnoreCase(method) && path.matches("/account/user/.*/role/\\w{5,9}/?"))
				|| ("DELETE".equalsIgnoreCase(method) && path.matches("/account/user/.*"));
	}

	private boolean checkAdminAccess(UserAccount userAccount) {
		return userAccount.getRoles().contains("Admin");
	}

}
