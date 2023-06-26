package telran.java47.security.enums;

public enum MethodsEnum {
	POST("POST"), GET("GET"), PUT("PUT"), DELETE("DELETE");

	private String title;

	MethodsEnum(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

}
