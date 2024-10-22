package model;

import chess.ChessGame;



public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
    private static int nextID = 0;

    public GameData(String whiteUsername, String blackUsername, String gameName, ChessGame game) {
        this(nextID++, whiteUsername, blackUsername, gameName, game);
    }
}
