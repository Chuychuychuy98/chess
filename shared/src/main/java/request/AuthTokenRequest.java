package request;

/**
 * Request class for endpoints which provide only an authToken.
 * @param authToken The user's authToken.
 */
public record AuthTokenRequest(String authToken) {
}
