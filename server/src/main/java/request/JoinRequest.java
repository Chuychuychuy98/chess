package request;

import chess.ChessGame;

/**
 * Request class for join endpoint.
 * @param playerColor The requested team playerColor.
 * @param gameID The ID of the game to be joined.
 */
public record JoinRequest(ChessGame.TeamColor playerColor, int gameID) {
}
