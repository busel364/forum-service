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
import telran.java47.post.dao.PostRepository;
import telran.java47.post.dto.exeptions.PostNotFoundExeption;
import telran.java47.post.model.Post;

@Component
@Order(25)
@RequiredArgsConstructor
public class AuthorFilter implements Filter {

	final UserAccountRepository userAccountRepository;
	final PostRepository postRepository;

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		if (checkEndPoint(request.getMethod(), request.getServletPath())) {
			String userName = "";
			String[] list = request.getServletPath().split("/");
			try {
				userName = request.getUserPrincipal().getName();
			} catch (Exception e) {
				throw new UserNotFoundExeption();
			}
			if (request.getMethod().equalsIgnoreCase("POST") && !list[3].equals(userName)) {
				response.sendError(403, "Forbiden");
				return;
			}
			if (request.getMethod().equalsIgnoreCase("PUT") && !list[5].equals(userName)) {
				response.sendError(403, "Forbiden");
				return;
			}

		}

		chain.doFilter(request, response);
	}

	private boolean checkEndPoint(String method, String path) {
		return ("POST".equalsIgnoreCase(method) && path.matches("/forum/post/\\w*"))
				|| ("PUT".equalsIgnoreCase(method) && path.matches("/forum/post/.*/comment/\\w*"));
	}

}
