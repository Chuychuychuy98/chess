package model;

import chess.*;
import com.google.gson.Gson;


public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {

    public GameData(String whiteUsername, String blackUsername, String gameName, ChessGame game) {
        this(0, whiteUsername, blackUsername, gameName, game);
    }

    public GameData(int id, String whiteUsername, String blackUsername, String gameName, String serializedGame) {
        this(id, whiteUsername, blackUsername, gameName, new Gson().fromJson(serializedGame, ChessGame.class));
    }

    /**
     * Return a copy of this GameData with the player of the given color replaced with the new username.
     * @param color The color to replace.
     * @param newUsername The username to replace the old one.
     * @return The new GameData.
     */
    public GameData newPlayer(ChessGame.TeamColor color, String newUsername) {
        if (color == ChessGame.TeamColor.BLACK) {
            return new GameData(gameID, whiteUsername, newUsername, gameName, game);
        }
        else {
            return new GameData(gameID, newUsername, blackUsername, gameName, game);
        }
    }

    public String serializedGame() {
        return new Gson().toJson(game);
    }

    public GameData removePlayer(String username) {
        if (this.blackUsername.equals(username)) {
            return new GameData(gameID, whiteUsername, null, gameName, game);
        }
        else if (this.whiteUsername.equals(username)) {
            return new GameData(gameID, null, blackUsername, gameName, game);
        }
        else {
            return this;
        }
    }

    public GameData makeMove(String username, ChessMove move) throws InvalidMoveException {
        if (username.equals(blackUsername)) {
            if (!game.getTeamTurn().equals(ChessGame.TeamColor.BLACK)) {
                throw new WrongTurnException(username);
            }
        }
        else if (username.equals(whiteUsername)) {
            if (!game.getTeamTurn().equals(ChessGame.TeamColor.WHITE)) {
                throw new WrongTurnException(username);
            }
        }
        else {
            throw new NonexistentPlayerException(username);
        }
        game().makeMove(move);
        return this;
    }
}
