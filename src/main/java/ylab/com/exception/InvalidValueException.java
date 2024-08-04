package ylab.com.exception;

public class InvalidValueException extends RuntimeException {
    public InvalidValueException(String fieldName, Object fieldValue) {
        super(String.format("Field %s, Error: must contain correct value, not %s ",
            fieldName, fieldValue.toString()));
    }
}
