package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import exceptions.DuplicateEntryException;
import exceptions.EntryNotFoundException;
import model.GameData;

public interface GameDAO {

    /**
     * Clear all data from the database.
     * @throws DataAccessException Indicates that the method failed to clear the database.
     */
    void clear() throws DataAccessException;

    /**
     * Add a new GameData to the database.
     * @param gameData The game to add.
     * @return The new game's ID.
     * @throws DuplicateEntryException Indicates that the gameID was already taken.
     * @throws DataAccessException Indicates an error reaching the database.
     */
    int createGame(GameData gameData) throws DataAccessException, DuplicateEntryException;

    /**
     * Retrieve a specified game with the given gameID.
     * @param gameID The ID of the game to retrieve.
     * @return GameData with the specified gameID.
     * @throws EntryNotFoundException Indicates that the gameID is not found in the database.
     * @throws DataAccessException Indicates an error reaching the database.
     */
    GameData getGame(int gameID) throws DataAccessException, EntryNotFoundException;

    /**
     * Retrieve all games.
     * @return All games in an array.
     * @throws DataAccessException Indicates an error reaching the database.
     */
    GameData[] listGames() throws DataAccessException;

    /**
     * Update the game with the given ID so that its player of the given playerColor now has the new username.
     * @param gameID The ID of the game being updated.
     * @param color The playerColor of the player being replaced.
     * @param newUsername The new username associated with the given playerColor.
     * @throws EntryNotFoundException Indicates the given gameID is not found in the database.
     * @throws DataAccessException Indicates an error reaching the database.
     */
    void updateGame(int gameID, ChessGame.TeamColor color, String newUsername) throws DataAccessException, EntryNotFoundException;

    void playerLeave(int gameID, String username) throws DataAccessException, EntryNotFoundException;

    void makeMove(int gameID, String username, ChessMove move) throws EntryNotFoundException, InvalidMoveException, DataAccessException;
}
