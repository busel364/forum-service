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
import telran.java47.accounting.model.UserAccount;
import telran.java47.security.enums.MethodsEnum;

@Component
@Order(15)
@RequiredArgsConstructor
public class UpdateUserByOwnerFilter implements Filter {

	final UserAccountRepository userAccountRepository;

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		String path = request.getServletPath();
		if (checkEndPoint(request.getMethod(), path)) {
			String[] list = request.getServletPath().split("/");
			UserAccount userAccount = userAccountRepository.findById(list[list.length - 1]).get();
			if (!request.getUserPrincipal().getName().equalsIgnoreCase(userAccount.getLogin())) {
				response.sendError(403);
				return;
			}
		}
		chain.doFilter(request, response);
	}

	private boolean checkEndPoint(String method, String path) {
		return (MethodsEnum.PUT.getTitle().equalsIgnoreCase(method) && path.matches("/account/user/\\w+/?"));
	}
}
