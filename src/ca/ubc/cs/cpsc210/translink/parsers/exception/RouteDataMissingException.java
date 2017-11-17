package ca.ubc.cs.cpsc210.translink.parsers.exception;

/**
 * Represents exception raised when expected data is missing from stop response.
 */
public class RouteDataMissingException extends Exception {
    public RouteDataMissingException() {
        super();
    }

    public RouteDataMissingException(String msg) {
        super(msg);
    }
}
