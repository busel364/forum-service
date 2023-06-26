package telran.java47.security.enums;

public enum RolesEnum {
	ADMINISTATOR("ADMINISTATOR"), MODERATOR("MODERATOR"), USER("USER");

	private String title;

	RolesEnum(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}
}
