package telran.java47.security.model;

import java.security.Principal;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Getter;
import telran.java47.security.enums.RolesEnum;

public class User implements Principal {

	String userName;
	@Getter
	Set<RolesEnum> roles;

	public User(String userName, Set<String> roles) {
		this.userName = userName;
		this.roles = roles.stream().map(v -> RolesEnum.valueOf(v.toUpperCase())).collect(Collectors.toSet());
	}

	@Override
	public String getName() {
		return userName;
	}

}
