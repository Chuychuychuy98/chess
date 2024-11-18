package request;

/**
 * Request class for create endpoint.
 * @param gameName The name of the game to be created.
 */
public record CreateGameRequest(String gameName) {
}
