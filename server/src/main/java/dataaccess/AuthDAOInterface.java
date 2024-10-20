package dataaccess;

import model.AuthData;

public interface AuthDAOInterface {

    /**
     * Clear all data from the database.
     */
    void clear();

    /**
     * Create a new authorization in the database.
     * @param authData The authData to add to the database.
     */
    void createAuth(AuthData authData);

    /**
     * Retrieve an authorization given an authToken.
     * @param authToken The token to search for.
     * @return AuthData for the requested authToken, or null if not found.
     */
    AuthData getAuth(String authToken);

    /**
     * Delete an authorization so that it is no longer valid.
     * @param authData The authorization to delete.
     */
    void deleteAuth(AuthData authData);

}
