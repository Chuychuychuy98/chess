package dataaccess;

import chess.ChessGame;
import exceptions.DuplicateEntryException;
import exceptions.EntryNotFoundException;
import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseGameDAO implements GameDAO {
    @Override
    public void clear() throws DataAccessException {

    }

    @Override
    public void createGame(GameData gameData) throws DataAccessException, DuplicateEntryException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement("SELECT gameID FROM game WHERE gameID=?")) {
                ps.setInt(1, gameData.gameID());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        throw new DuplicateEntryException(String.format("Game with id %d already exists.", gameData.gameID()));
                    }
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO game (gameID, whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)")) {
                ps.setInt(1, gameData.gameID());
                ps.setString(2, gameData.whiteUsername());
                ps.setString(3, gameData.blackUsername());
                ps.setString(4, gameData.gameName());
                ps.setString(5, gameData.serializedGame());

                ps.executeUpdate();
            }
        }
        catch (SQLException ex) {
            throw new DataAccessException(String.format("Error: %s", ex.getMessage()));
        }
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
