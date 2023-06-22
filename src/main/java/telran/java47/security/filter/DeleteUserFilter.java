package telran.java47.security.filter;

import java.io.IOException;
import java.security.Principal;

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
import telran.java47.accounting.model.UserAccount;

@Component
@Order(25)
@RequiredArgsConstructor
public class DeleteUserFilter implements Filter {

	final UserAccountRepository userAccountRepository;

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		String path = request.getServletPath();
		if (checkEndPoint(request.getMethod(), path)) {
			Principal principal = request.getUserPrincipal();
			String[] arr = path.split("/");
			String user = arr[arr.length - 1];
			UserAccount userAccount = userAccountRepository.findById(principal.getName()).get();
			if (!(principal.getName().equalsIgnoreCase(user) || userAccount.getRoles().contains("Administrator".toUpperCase()))) {
				response.sendError(403);
				return;
			}
			
		}
		chain.doFilter(request, response);
	}

	private boolean checkEndPoint(String method, String path) {
		return "DELETE".equalsIgnoreCase(method) && path.matches("/account/user/\\w+/?");
	}

}
