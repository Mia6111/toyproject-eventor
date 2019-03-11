package me.toyproject.mia.converter;

import static me.toyproject.mia.account.UserNotificationMethod.*;
import static me.toyproject.mia.converter.UserNotificationMethodConverter.DELIMITER;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import me.toyproject.mia.account.UserNotificationMethod;
import org.assertj.core.api.SoftAssertions;
import org.junit.Before;
import org.junit.Test;

public class UserNotificationMethodConverterTest {

	private UserNotificationMethodConverter converter;

	@Before
	public void setup() {
		converter = new UserNotificationMethodConverter();
	}

	@Test
	public void convertToDatabaseColumn() {
		SoftAssertions softAssertions = new SoftAssertions();
		softAssertions.assertThat(converter.convertToDatabaseColumn(null)).isEqualTo(NONE.name());
		softAssertions.assertThat(converter.convertToDatabaseColumn(Collections.emptySet())).isEqualTo(NONE.name());
		softAssertions.assertThat(converter.convertToDatabaseColumn(EnumSet.of(EMAIL, MOBILE_MSG, SLACK)))
			.isEqualTo(EMAIL.name() + DELIMITER + MOBILE_MSG.name() + DELIMITER + SLACK.name());

		softAssertions.assertAll();
	}

	@Test
	public void convertToEntityAttribute() {
		String allMethodsStr = EMAIL.name() + DELIMITER + MOBILE_MSG.name() + DELIMITER + SLACK.name();
		Set<UserNotificationMethod> result = converter.convertToEntityAttribute(allMethodsStr);
		assertThat(result).contains(EMAIL, MOBILE_MSG, SLACK);
		assertThat(result).doesNotContain(NONE);
	}
	@Test(expected = IllegalArgumentException.class)
	public void convertToEntityAttribute_없는_ENUM_Value일때() {
		String allMethodsStr = "없는ENUM";
		Set<UserNotificationMethod> result = converter.convertToEntityAttribute(allMethodsStr);
	}
}