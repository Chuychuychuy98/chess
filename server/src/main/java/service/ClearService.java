package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;

/**
 * Service class for clear endpoint.
 */
public class ClearService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final UserDAO userDAO;

    /**
     * Service class for clear endpoint.
     * @param authDAO AuthData DAO to use.
     * @param gameDAO GameData DAO to use.
     * @param userDAO UserData DAO to use.
     */
    public ClearService(AuthDAO authDAO, GameDAO gameDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
        this.userDAO = userDAO;
    }

    /**
     * Clear data from all databases.
     * @throws DataAccessException Indicates error reaching the database.
     */
    public void clear() throws DataAccessException{
        authDAO.clear();
        gameDAO.clear();
        userDAO.clear();
    }
}
