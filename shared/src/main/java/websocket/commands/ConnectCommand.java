package websocket.commands;

import chess.ChessGame;

public class ConnectCommand extends UserGameCommand {

    private final ChessGame.TeamColor color;

    public ConnectCommand(String authToken, Integer gameID) {
        super(CommandType.CONNECT, authToken, gameID);
        this.color = null;
    }

    public ConnectCommand(String authToken, Integer gameID, ChessGame.TeamColor color) {
        super(CommandType.CONNECT, authToken, gameID);
        this.color = color;
    }

    public ChessGame.TeamColor getColor() {
        return color;
    }
}
