package ca.ubc.cs.cpsc210.translink.model.exception;

/**
 * Represents exception raised when errors occur with Stops
 */
public class StopException extends Exception {
    public StopException(String msg) {
        super(msg);
    }
}

