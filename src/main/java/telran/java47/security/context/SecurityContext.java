package telran.java47.security.context;

import telran.java47.security.model.User;

public interface SecurityContext {

	User addUserSession(User user, String sessionId);

	User removeUser(String sessionId);

	User getUserBySessionId(String sessionId);
}
