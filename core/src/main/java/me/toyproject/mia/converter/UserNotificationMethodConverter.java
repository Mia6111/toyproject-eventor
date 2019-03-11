package me.toyproject.mia.converter;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.AttributeConverter;
import me.toyproject.mia.account.UserNotificationMethod;
import org.springframework.util.CollectionUtils;

public class UserNotificationMethodConverter implements AttributeConverter<Set<UserNotificationMethod>, String>{

	static final String DELIMITER = ",";

	@Override
	public String convertToDatabaseColumn(Set<UserNotificationMethod> attribute) {
		if(CollectionUtils.isEmpty(attribute)){
			return UserNotificationMethod.NONE.name();
		}
		return attribute.stream().map(UserNotificationMethod::name).sorted().collect(Collectors.joining(DELIMITER));
	}

	@Override
	public Set<UserNotificationMethod> convertToEntityAttribute(String dbData) {
		return Arrays.stream(dbData.split(DELIMITER)).map(UserNotificationMethod::valueOf)
			.collect(Collectors.toCollection(() -> EnumSet.noneOf(UserNotificationMethod.class)));
	}
}
