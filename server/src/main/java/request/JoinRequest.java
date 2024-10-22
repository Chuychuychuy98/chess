package request;

import chess.ChessGame;

/**
 * Request class for join endpoint.
 * @param color The requested team color.
 * @param gameID The ID of the game to be joined.
 */
public record JoinRequest(ChessGame.TeamColor color, int gameID) {
}
