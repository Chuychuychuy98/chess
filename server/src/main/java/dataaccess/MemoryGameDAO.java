package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.HashMap;
import java.util.Map;

/**
 * Memory implementation of GameDAO
 */
public class MemoryGameDAO implements GameDAO{

    Map<Integer, GameData> database = new HashMap<>();
    @Override
    public void clear() {
        database.clear();
    }

    @Override
    public void createGame(GameData gameData) throws DuplicateEntryException {
        if (database.get(gameData.gameID()) != null) throw new DuplicateEntryException(String.format("Game with id %d already exists.", gameData.gameID()));
        database.put(gameData.gameID(), gameData);
    }

    @Override
    public GameData getGame(int gameID) throws EntryNotFoundException {
        GameData data = database.get(gameID);
        if (data == null) throw new EntryNotFoundException(String.format("Game with ID %d not found.", gameID));
        return data;
    }

    @Override
    public GameData[] listGames() {
        return database.values().toArray(new GameData[0]);
    }

    @Override
    public void updateGame(int gameID, ChessGame.TeamColor color, String newUsername) throws EntryNotFoundException {
        GameData data = database.get(gameID);
        if (data == null) throw new EntryNotFoundException(String.format("Game with id %d not found.", gameID));
        database.put(data.gameID(), data.newPlayer(color, newUsername));
    }
}
