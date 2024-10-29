package request;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Request class for login endpoint.
 * @param username The username trying to login.
 * @param password The user's password, which is hashed before being stored.
 */
public record LoginRequest(String username, String password) {
    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = BCrypt.hashpw(password, BCrypt.gensalt());
    }
}
