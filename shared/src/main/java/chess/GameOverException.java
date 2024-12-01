package chess;

public class GameOverException extends InvalidMoveException {
    public GameOverException() {
        super("Game is already over.");
    }
}
