package me.toyproject.mia.common;

import lombok.Value;
import org.apache.logging.log4j.util.Strings;

@Value
public class ValidationErrorMessage {
    private String objectName;
    private String field;
    private String message;

    public static ValidationErrorMessage empty(String objectName){
        return new ValidationErrorMessage(objectName, Strings.EMPTY, Strings.EMPTY);
    }


}
