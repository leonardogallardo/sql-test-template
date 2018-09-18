package exception;

public class InesperadoException extends RuntimeException {


    public InesperadoException(final String message, final Exception e) {
        super(message, e);
    }

    public InesperadoException(final Exception e) {
        super(e);
    }

    public InesperadoException(final String message) {
        super(message);
    }

}
