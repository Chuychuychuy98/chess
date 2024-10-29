package request;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Request class for the register endpoint.
 * @param username The requested username.
 * @param password The new user's password, which is hashed before being stored.
 * @param email The new user's email address.
 */
public record RegisterRequest(String username, String password, String email) {
    public RegisterRequest(String username, String password, String email) {
        this.username = username;
        this.password = BCrypt.hashpw(password, BCrypt.gensalt());
        this.email = email;
    }
}
