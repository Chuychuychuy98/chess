package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;

/**
 * Service class for list, create, and join endpoints
 */
public class GameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    /**
     * Service class for list, create, and join endpoints.
     * @param gameDAO GameData DAO to use.
     * @param authDAO AuthData DAO to use.
     */
    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }
}
