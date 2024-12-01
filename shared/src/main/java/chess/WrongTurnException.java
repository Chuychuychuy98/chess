package chess;

public class WrongTurnException extends InvalidMoveException {
    public WrongTurnException() {
        super("That piece belongs to the other team.");
    }
    public WrongTurnException(String username) {
        super("It is not " + username + "'s turn.");
    }
}
