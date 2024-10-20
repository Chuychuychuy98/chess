package dataaccess;

import model.AuthData;

public interface AuthDAO {

    /**
     * Clear all data from the database.
     * @throws DataAccessException Indicates an error reaching the database.
     */
    void clear() throws DataAccessException;

    /**
     * Create a new authorization in the database.
     * @param authData The authData to add to the database.
     * @throws DataAccessException Indicates an error reaching the database.
     * @throws DuplicateEntryException Indicates that the given username is already associated with an authToken.
     */
    void createAuth(AuthData authData) throws DataAccessException, DuplicateEntryException;

    /**
     * Retrieve an authorization given an authToken.
     * @param authToken The token to search for.
     * @return AuthData for the requested authToken.
     * @throws EntryNotFoundException Indicates that the given authToken is not found in the database.
     * @throws DataAccessException Indicates an error reaching the database.
     */
    AuthData getAuth(String authToken) throws DataAccessException, EntryNotFoundException;

    /**
     * Delete an authorization so that it is no longer valid.
     * @param authData The authorization to delete.
     * @throws EntryNotFoundException Indicates that the given authData is not found in the database.
     * @throws DataAccessException Indicates an error reaching the database.
     */
    void deleteAuth(AuthData authData) throws DataAccessException, EntryNotFoundException;

}
