package dataaccess;

import chess.ChessGame;
import model.GameData;

public interface GameDAOInterface {

    /**
     * Clear all data from the database.
     * @throws DataAccessException Indicates that the method failed to clear the database.
     */
    void clear() throws DataAccessException;

    /**
     * Add a new GameData to the database.
     * @param gameData The game to add.
     * @throws DuplicateEntryException Indicates that the gameID was already taken.
     * @throws DataAccessException Indicates an error reaching the database.
     */
    void createGame(GameData gameData) throws DataAccessException;

    /**
     * Retrieve a specified game with the given gameID.
     * @param gameID The ID of the game to retrieve.
     * @return GameData with the specified gameID.
     * @throws EntryNotFoundException Indicates that the gameID is not found in the database.
     * @throws DataAccessException Indicates an error reaching the database.
     */
    GameData getGame(int gameID) throws DataAccessException;

    /**
     * Retrieve all games.
     * @return All games in an array.
     * @throws DataAccessException Indicates an error reaching the database.
     */
    GameData[] listGames() throws DataAccessException;

    /**
     * Replace the chess game with the given gameID with the provided game.
     * @param gameID The ID of the game being replaced.
     * @param game The game which will replace the old one.
     * @throws EntryNotFoundException Indicates the given gameID is not found in the database.
     * @throws DataAccessException Indicates an error reaching the database.
     */
    void updateGame(int gameID, ChessGame game) throws DataAccessException;

}
