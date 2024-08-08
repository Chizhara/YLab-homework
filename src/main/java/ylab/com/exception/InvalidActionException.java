package ylab.com.exception;

/**
 * {@code NotFoundException} вызывается при попытке невозможного действия
 */
public class InvalidActionException extends RuntimeException {
    public InvalidActionException(String msg) {
        super(msg);
    }
}
