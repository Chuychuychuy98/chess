package dataaccess;

import exceptions.DuplicateEntryException;
import exceptions.UnauthorizedException;
import model.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseAuthDAO implements AuthDAO {
    @Override
    public void clear() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement("TRUNCATE TABLE auth")) {
                ps.executeUpdate();
            }
        }
        catch (SQLException ex) {
            throw new DataAccessException("Error: could not connect to database");
        }
    }

    @Override
    public void createAuth(AuthData authData) throws DataAccessException, DuplicateEntryException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement("SELECT authtoken FROM auth WHERE authtoken = ?")) {
                ps.setString(1, authData.authToken());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        throw new DuplicateEntryException("Error: authToken already in use");
                    }
                }
            }
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO auth (authtoken, username) VALUES (?, ?)")) {
                ps.setString(1, authData.authToken());
                ps.setString(2, authData.username());

                ps.executeUpdate();
            }
        }
        catch (SQLException ex) {
            throw new DataAccessException("Error: could not connect to database");
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException, UnauthorizedException {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException, UnauthorizedException {

    }

    @Override
    public void checkAuth(String authToken) throws DataAccessException, UnauthorizedException {

    }
}
