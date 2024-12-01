package chess;

public class NonexistentPlayerException extends InvalidMoveException {
    public NonexistentPlayerException(String username) {
        super(username + " is not playing.");
    }
}
