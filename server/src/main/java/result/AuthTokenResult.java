package result;

/**
 * Result class for endpoints that return a username and authToken.
 * @param username The username being returned.
 * @param authToken The authToken being returned.
 */
public record AuthTokenResult(String username, String authToken) {
}
