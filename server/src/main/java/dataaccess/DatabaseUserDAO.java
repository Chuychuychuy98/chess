package dataaccess;

import exceptions.DuplicateEntryException;
import exceptions.EntryNotFoundException;
import exceptions.UnauthorizedException;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseUserDAO implements UserDAO {
    @Override
    public void clear() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement("TRUNCATE TABLE user")) {
                ps.executeUpdate();
            }
        }
        catch (SQLException ex) {
            throw new DataAccessException("Error: could not connect to database");
        }
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException, DuplicateEntryException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement("SELECT username FROM user WHERE username=?")) {
                ps.setString(1, userData.username());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        throw new DuplicateEntryException("Error: already taken");
                    }
                }
            }
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO user (username, password, email) VALUES (?, ?, ?)")) {
                ps.setString(1, userData.username());
                ps.setString(2, BCrypt.hashpw(userData.password(), BCrypt.gensalt()));
                ps.setString(3, userData.email());

                ps.executeUpdate();
            }
        }
        catch (SQLException ex) {
            throw new DataAccessException(String.format("Error: %s", ex.getMessage()));
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException, EntryNotFoundException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement("SELECT username, password, email FROM user WHERE username=?")) {
                ps.setString(1, username);
                ResultSet rs = ps.executeQuery();
                if (!rs.next()) {
                    throw new EntryNotFoundException(String.format("No user found with username %s.", username));
                }
                return new UserData(rs.getString("username"), rs.getString("password"), rs.getString("email"));
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Error: %s", ex.getMessage()));
        }
    }

    @Override
    public void removeUser(String username) throws DataAccessException, EntryNotFoundException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement("SELECT username FROM user WHERE username=?")) {
                ps.setString(1, username);
                ResultSet rs = ps.executeQuery();
                if (!rs.next()) {
                    throw new EntryNotFoundException(String.format("No user found with username %s.", username));
                }
            }
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM user WHERE username=?")) {
                ps.setString(1, username);
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Error: %s", ex.getMessage()));
        }
    }
}
