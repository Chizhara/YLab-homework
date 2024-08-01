package ylab.com.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(Object entity, Object idValue) {
        super(String.format("%s with id=%s was not found", entity.getClass().getName(), idValue));
    }

    public NotFoundException(Class entityClassName, Object idValue) {
        super(String.format("%s with id=%s was not found", entityClassName.getName(), idValue));
    }

}