package request;

/**
 * Request class for create endpoint.
 * @param authToken The authToken to validate.
 * @param gameName The name of the game to be created.
 */
public record CreateGameRequest(String authToken, String gameName) {
}
