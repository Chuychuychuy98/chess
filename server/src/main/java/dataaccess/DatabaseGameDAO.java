package dataaccess;

import chess.ChessGame;
import exceptions.DuplicateEntryException;
import exceptions.EntryNotFoundException;
import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DatabaseGameDAO implements GameDAO {
    @Override
    public void clear() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement("TRUNCATE TABLE game")) {
                ps.executeUpdate();
            }
        }
        catch (SQLException ex) {
            throw new DataAccessException("Could not connect to database.");
        }
    }

    @Override
    public int createGame(GameData gameData) throws DataAccessException, DuplicateEntryException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement("SELECT gameID FROM game WHERE gameID=?")) {
                ps.setInt(1, gameData.gameID());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        throw new DuplicateEntryException(String.format("Game with id %d already exists.", gameData.gameID()));
                    }
                }
            }
            if (gameData.gameID() == 0) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO game (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)",
                        PreparedStatement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, gameData.whiteUsername());
                    ps.setString(2, gameData.blackUsername());
                    ps.setString(3, gameData.gameName());
                    ps.setString(4, gameData.serializedGame());

                    ps.executeUpdate();
                    ResultSet res = ps.getGeneratedKeys();
                    if (res.next()) {
                        return res.getInt(1);
                    }
                    return 0;
                }
            }
            else {
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO game (gameID, whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)")) {
                    ps.setInt(1, gameData.gameID());
                    ps.setString(2, gameData.whiteUsername());
                    ps.setString(3, gameData.blackUsername());
                    ps.setString(4, gameData.gameName());
                    ps.setString(5, gameData.serializedGame());

                    ps.executeUpdate();
                    return gameData.gameID();
                }
            }
        }
        catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException, EntryNotFoundException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM game WHERE gameID=?")) {
                ps.setInt(1, gameID);
                ResultSet rs = ps.executeQuery();
                if (!rs.next()) {
                    throw new EntryNotFoundException(String.format("Game with ID %d not found.", gameID));
                }
                return new GameData(gameID,
                        rs.getString("whiteUsername"),
                        rs.getString("blackUsername"),
                        rs.getString("gameName"),
                        rs.getString("game"));
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public GameData[] listGames() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement("SELECT gameID, whiteUsername, blackUsername, gameName, game FROM game")) {
                ResultSet rs = ps.executeQuery();
                ArrayList<GameData> games = new ArrayList<>();
                while (rs.next()) {
                    games.add(new GameData(
                            rs.getInt("gameID"),
                            rs.getString("whiteUsername"),
                            rs.getString("blackUsername"),
                            rs.getString("gameName"),
                            rs.getString("game")
                    ));
                }
                return games.toArray(new GameData[0]);
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public void updateGame(int gameID, ChessGame.TeamColor color, String newUsername) throws DataAccessException, EntryNotFoundException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM game WHERE gameID=?")) {
                ps.setInt(1, gameID);
                ResultSet rs = ps.executeQuery();
                if (!rs.next()) {
                    throw new EntryNotFoundException(String.format("Game with ID %d not found.", gameID));
                }
                if (color == ChessGame.TeamColor.BLACK) {
                    try (PreparedStatement update = conn.prepareStatement("UPDATE game SET blackUsername=? WHERE gameID=?")) {
                        update.setString(1, newUsername);
                        update.setInt(2, gameID);
                        update.executeUpdate();
                    }
                }
                else {
                    try (PreparedStatement update = conn.prepareStatement("UPDATE game SET whiteUsername=? WHERE gameID=?")) {
                        update.setString(1, newUsername);
                        update.setInt(2, gameID);
                        update.executeUpdate();
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public void playerLeave(int gameID, String username) throws DataAccessException, EntryNotFoundException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM game WHERE gameID=?")) {
                ps.setInt(1, gameID);
                ResultSet rs = ps.executeQuery();
                if (!rs.next()) {
                    throw new EntryNotFoundException(String.format("Game with id %d not found.", gameID));
                }
                GameData data = new GameData(
                        rs.getInt("gameID"),
                        rs.getString("whiteUsername"),
                        rs.getString("blackUsername"),
                        rs.getString("gameName"),
                        rs.getString("game")
                );
                if (data.whiteUsername().equals(username)) {
                    try (PreparedStatement update = conn.prepareStatement("UPDATE game SET whiteUsername=NULL WHERE gameID=?")) {
                        update.setInt(1, gameID);
                        update.executeUpdate();
                    }
                }
                else if (data.blackUsername().equals(username)) {
                    try (PreparedStatement update = conn.prepareStatement("UPDATE game SET blackUsername=NULL WHERE gameID=?")) {
                        update.setInt(1, gameID);
                        update.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
