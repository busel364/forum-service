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

import telran.java47.security.enums.RolesEnum;
import telran.java47.security.model.User;

@Component
@Order(20)
public class RoleManagingFilter implements Filter {

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		if (checkEndPoint(request.getMethod(), request.getServletPath())) {
			User user = (User) request.getUserPrincipal();
			if (!user.getRoles().contains(RolesEnum.ADMINISTATOR.getTitle())) {
				response.sendError(403);
				return;
			}
		}
		chain.doFilter(request, response);
	}

	private boolean checkEndPoint(String method, String path) {
		return path.matches("/account/user/\\w+/role/\\w+/?");
	}
}
