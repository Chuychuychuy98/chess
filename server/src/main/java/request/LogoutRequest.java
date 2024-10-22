package request;

/**
 * Request class for the logout endpoint.
 * @param authToken The user's authToken.
 */
public record LogoutRequest(String authToken) {
}
