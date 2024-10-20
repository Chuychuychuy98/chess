package dataaccess;

import model.UserData;

public interface UserDAOInterface {

    /**
     * Clear all data from the database.
     */
    void clear();

    /**
     * Create a new user.
     * @param username The user's username.
     * @param password The user's password.
     * @param email The user's email address.
     * @return UserData representing the new user.
     */
    UserData createUser(String username, String password, String email);

    /**
     * Retrieve a user with the given username.
     * @param username The username to retrieve.
     * @return Userdata for the requested user, or null if not found.
     */
    UserData getUser(String username);
}
