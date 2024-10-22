package result;

import model.AuthData;

/**
 * Result class for endpoints that return a username and authToken.
 * @param username The username being returned.
 * @param authToken The authToken being returned.
 */
public record AuthTokenResult(String username, String authToken) {

    /**
     * Construct an AuthTokenResult from the given AuthData.
     * @param data The AuthData to construct the AuthTokenResult from.
     */
    public AuthTokenResult(AuthData data) {
        this(data.username(), data.authToken());
    }
}
