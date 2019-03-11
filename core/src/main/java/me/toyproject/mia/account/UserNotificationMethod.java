package me.toyproject.mia.account;

import java.util.EnumSet;
import java.util.Set;
import lombok.Getter;

@Getter
public enum UserNotificationMethod {
	NONE("없음", null),
	EMAIL("이메일", "email"),
	MOBILE_MSG("문자 메세지", "mobile"),
	SLACK("슬랙", "email");

	private String description;
	private String requiredField; // Field in Account.class

	UserNotificationMethod(String description, String requiredField) {
		this.description = description;
		this.requiredField = requiredField;
	}
	public static final Set<UserNotificationMethod> NO_METHDS = EnumSet.of(NONE);
	public static final Set<UserNotificationMethod> DEFUALT = EnumSet.of(EMAIL);
}
