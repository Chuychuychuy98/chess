package model;

import chess.ChessGame;
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
}
