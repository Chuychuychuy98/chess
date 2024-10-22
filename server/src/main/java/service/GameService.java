package service;

import chess.ChessGame;
import dataaccess.*;
import exceptions.DuplicateEntryException;
import exceptions.EntryNotFoundException;
import exceptions.TeamColorTakenException;
import exceptions.UnauthorizedException;
import model.AuthData;
import model.GameData;
import request.AuthTokenRequest;
import request.CreateGameRequest;
import request.JoinRequest;
import result.CreateGameResult;
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

    /**
     * Create a new chess game and add it to the database.
     * @param request Request containing the name of the new game.
     * @param authTokenRequest Request containing the authToken to validate.
     * @return CreateGameResult containing the new game's ID.
     * @throws DataAccessException Indicates an error reaching the database.
     * @throws UnauthorizedException Indicates that the given authToken was not found in the database.
     * @throws DuplicateEntryException Indicates that the gameID was not unique.
     */
    public CreateGameResult create(CreateGameRequest request, AuthTokenRequest authTokenRequest) throws DataAccessException, UnauthorizedException, DuplicateEntryException {
        authDAO.checkAuth(authTokenRequest.authToken());
        GameData newGame = new GameData(null, null, request.gameName(), new ChessGame());
        gameDAO.createGame(newGame);
        return new CreateGameResult(newGame.gameID());
    }

    /**
     * Add a player to a game.
     * @param request JoinRequest containing the gameID to join and the playerColor to join as.
     * @param authTokenRequest AuthTokenRequest containing the authToken to validate.
     * @throws DataAccessException Indicates an error reaching the database.
     * @throws UnauthorizedException Indicates that the authToken is not found in the database.
     * @throws TeamColorTakenException Indicates that the requested playerColor is not available for the requested game.
     * @throws EntryNotFoundException Indicates that the gameID is not found in the database.
     */
    public void join(JoinRequest request, AuthTokenRequest authTokenRequest) throws DataAccessException, UnauthorizedException, TeamColorTakenException, EntryNotFoundException {
        AuthData auth = authDAO.getAuth(authTokenRequest.authToken());
        GameData game = gameDAO.getGame(request.gameID());
        if ((request.playerColor() == ChessGame.TeamColor.BLACK && game.blackUsername() != null) ||
            (request.playerColor() == ChessGame.TeamColor.WHITE && game.whiteUsername() != null)) {
            throw new TeamColorTakenException("Error: already taken");
        }
        gameDAO.updateGame(request.gameID(), request.playerColor(), auth.username());
    }
}
