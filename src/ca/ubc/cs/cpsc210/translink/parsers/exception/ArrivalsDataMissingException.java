package ca.ubc.cs.cpsc210.translink.parsers.exception;

/**
 * Represents exception raised when expected data is missing from stop estimate response.
 */
public class ArrivalsDataMissingException extends Exception {
    public ArrivalsDataMissingException() {
        super();
    }

    public ArrivalsDataMissingException(String msg) {
        super(msg);
    }
}
