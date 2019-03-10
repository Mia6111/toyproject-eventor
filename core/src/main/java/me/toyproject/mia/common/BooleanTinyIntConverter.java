package me.toyproject.mia.common;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * Boolean 필드값 <-> Integer DB 컬럼 (null 값이 들어가는 경우 없음)
 */
@Converter(autoApply = true)
public class BooleanTinyIntConverter implements AttributeConverter<Boolean, Integer> {

	@Override
	public Integer convertToDatabaseColumn(Boolean attribute) {
		return (attribute == Boolean.TRUE) ?  1 : 0;
	}

	@Override
	public Boolean convertToEntityAttribute(Integer dbData) {
		return dbData == NumberUtils.INTEGER_ONE ? Boolean.TRUE : Boolean.FALSE;
	}
}
