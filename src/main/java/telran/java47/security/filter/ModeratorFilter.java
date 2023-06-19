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
@Order(20)
@RequiredArgsConstructor
public class ModeratorFilter implements Filter {

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
			Post post = postRepository.findById(list[2]).orElseThrow(PostNotFoundExeption::new);
			UserAccount userAccount = userAccountRepository.findById(userName).orElseThrow(UserNotFoundExeption::new);
			if (checkOwner(request, userAccount, post)) {
				chain.doFilter(request, response);
				return;
			}
			if (!checkModeratorAccess(userAccount)) {
				response.sendError(403, "Forbiden");
				return;
			}
		}

		chain.doFilter(request, response);
	}

	private boolean checkOwner(HttpServletRequest request, UserAccount user, Post post) {
		return (user.getLogin().equals(post.getAuthor()))
				&& checkEndPoint(request.getMethod(), request.getServletPath());
	}

	private boolean checkEndPoint(String method, String path) {
		return ("DELETE".equalsIgnoreCase(method) && path.matches("forum/post/.*"))
				|| ("PUT".equalsIgnoreCase(method) && path.matches("forum/post/.*"));
	}

	private boolean checkModeratorAccess( UserAccount userAccount) {
		return (userAccount.getRoles().contains("Moderator"));
	}

}
