package dataaccess;

import chess.ChessGame;
import exceptions.DuplicateEntryException;
import exceptions.EntryNotFoundException;
import model.GameData;

public class DatabaseGameDAO implements GameDAO {
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
    public void updateGame(int gameID, ChessGame.TeamColor color, String newUsername) throws DataAccessException, EntryNotFoundException {

    }
}
