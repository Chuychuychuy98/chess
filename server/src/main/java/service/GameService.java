package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UnauthorizedException;
import request.AuthTokenRequest;
import result.ListResult;

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

    /**
     * Return a list of all current games in the database.
     * @param request AuthTokenRequest containing the authToken to validate.
     * @return ListResult of all games in the database.
     * @throws DataAccessException Indicates an error reaching the database.
     * @throws UnauthorizedException Indicates that the authToken is not found in the database.
     */
    public ListResult list(AuthTokenRequest request) throws DataAccessException, UnauthorizedException {
        authDAO.checkAuth(request.authToken());
        return new ListResult(gameDAO.listGames());
    }
}
