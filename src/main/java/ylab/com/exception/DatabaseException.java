package ylab.com.exception;

/**
 * {@code NotFoundException} вызывается при возникновении ошибок при работе с базой данных
 */
public class DatabaseException extends RuntimeException {
    public DatabaseException(Throwable cause) {
        super(cause);
    }
}
