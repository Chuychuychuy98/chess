package dataaccess;

import model.UserData;

public interface UserDAOInterface {

    /**
     * Clear all data from the database.
     */
    void clear();

    /**
     * Create a new user.
     * @param userData The data to add to the database.
     */
    void createUser(UserData userData);

    /**
     * Retrieve a user with the given username.
     * @param username The username to retrieve.
     * @return Userdata for the requested user, or null if not found.
     */
    UserData getUser(String username);
}
