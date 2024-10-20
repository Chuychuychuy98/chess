package dataaccess;

import chess.ChessGame;
import model.GameData;

/**
 * Memory implementation of GameDAO
 */
public class MemoryGameDAO implements GameDAO{
    @Override
    public void clear() throws DataAccessException {

    }

    @Override
    public void createGame(GameData gameData) throws DataAccessException, DuplicateEntryException {

    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException, EntryNotFoundException {
        return null;
    }

    @Override
    public GameData[] listGames() throws DataAccessException {
        return new GameData[0];
    }

    @Override
    public void updateGame(int gameID, ChessGame game) throws DataAccessException, EntryNotFoundException {

    }
}
