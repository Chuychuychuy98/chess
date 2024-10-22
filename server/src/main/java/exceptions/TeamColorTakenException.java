package exceptions;

/**
 * Indicates that the request to join a game could not be fulfilled because the requested team color was already taken.
 */
public class TeamColorTakenException extends Exception{
    public TeamColorTakenException(String message) {
        super(message);
    }
}
