package model;

import chess.ChessGame;



public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
    private static int nextID = 0;

    public GameData(String whiteUsername, String blackUsername, String gameName, ChessGame game) {
        this(nextID++, whiteUsername, blackUsername, gameName, game);
    }

    public boolean equalsNoID(GameData other) {
            return other.whiteUsername.equals(this.whiteUsername) &&
                    other.blackUsername.equals(this.blackUsername) &&
                    other.gameName.equals(this.gameName) &&
                    other.game.equals(this.game);
    }
}
