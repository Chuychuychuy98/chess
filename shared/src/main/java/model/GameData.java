package model;

import chess.ChessGame;



public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
    private static int nextID = 0;

    public GameData(String whiteUsername, String blackUsername, String gameName, ChessGame game) {
        this(nextID++, whiteUsername, blackUsername, gameName, game);
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
}
