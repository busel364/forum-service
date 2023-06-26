package telran.java47.security.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import telran.java47.security.model.User;

@Component
public class SecurityContextImpl implements SecurityContext {

	Map<String, User> context = new ConcurrentHashMap<>();

	@Override
	public User addUserSession(User user, String sessionId) {
		return context.put(sessionId, user);
	}

	@Override
	public User removeUser(String sessionId) {
		return context.remove(sessionId);
	}

	@Override
	public User getUserBySessionId(String sessionId) {
		return context.get(sessionId);
	}

}