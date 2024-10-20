package dataaccess;

import model.UserData;

public interface UserDAOInterface {

    /**
     * Clear all data from the database.
     * @throws DataAccessException Indicates an error reaching the database.
     */
    void clear() throws DataAccessException;

    /**
     * Create a new user.
     * @param userData The data to add to the database.
     * @throws DuplicateEntryException Indicates the username is already found in the database.
     * @throws DataAccessException Indicates an error reaching the database.
     */
    void createUser(UserData userData) throws DataAccessException, DuplicateEntryException;

    /**
     * Retrieve a user with the given username.
     * @param username The username to retrieve.
     * @return Userdata for the requested user, or null if not found.
     * @throws EntryNotFoundException Indicates that the given username is not found in the database.
     * @throws DataAccessException Indicates an error reaching the database.
     */
    UserData getUser(String username) throws DataAccessException, EntryNotFoundException;
}
