package ca.ubc.cs.cpsc210.translink.parsers.exception;

/**
 * Represents exception raised when expected data is missing from stop response.
 */
public class StopDataMissingException extends Exception {
    public StopDataMissingException() {
        super();
    }

    public StopDataMissingException(String msg) {
        super(msg);
    }
}
