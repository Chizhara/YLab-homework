package ylab.com.exception;

/**
 * {@code NotFoundException} вызывается при попытке достать несуществующий объект
 *
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(Class entityClassName, Object idValue) {
        super(String.format("%s with id=%s was not found", entityClassName.getName(), idValue));
    }

}