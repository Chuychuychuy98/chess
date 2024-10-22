package request;

/**
 * Request class for the logout endpoint.
 * @param authToken The user's authToken.
 */
public record AuthTokenRequest(String authToken) {
}
