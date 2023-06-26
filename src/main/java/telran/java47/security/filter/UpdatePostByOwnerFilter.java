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
import telran.java47.post.dao.PostRepository;
import telran.java47.post.model.Post;
import telran.java47.security.enums.MethodsEnum;

@Component
@Order(35)
@RequiredArgsConstructor
public class UpdatePostByOwnerFilter implements Filter {

	final UserAccountRepository userAccountRepository;
	final PostRepository postRepository;

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		if (checkEndPoint(request.getMethod(), request.getServletPath())) {
			String[] list = request.getServletPath().split("/");
			Post post = postRepository.findById(list[list.length - 1]).orElse(null);
			if (post == null || !post.getAuthor().equalsIgnoreCase(request.getUserPrincipal().getName())) {
				response.sendError(403);
				return;
			}
		}
		chain.doFilter(request, response);

	}

	private boolean checkEndPoint(String method, String path) {
		return (MethodsEnum.PUT.getTitle().equalsIgnoreCase(method) && path.matches("forum/post/\\w+"));
	}

}
