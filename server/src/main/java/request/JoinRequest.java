package request;

import chess.ChessGame;

/**
 * Request class for join endpoint.
 * @param authToken The authToken to validate.
 * @param color The requested team color.
 * @param gameID The ID of the game to be joined.
 */
public record JoinRequest(String authToken, ChessGame.TeamColor color, int gameID) {
}
