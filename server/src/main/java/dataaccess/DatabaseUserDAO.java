package dataaccess;

import exceptions.DuplicateEntryException;
import exceptions.EntryNotFoundException;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseUserDAO implements UserDAO {
    @Override
    public void clear() throws DataAccessException {

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
                ps.setString(2, userData.password());
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
        return null;
    }

    @Override
    public void removeUser(String username) throws DataAccessException, EntryNotFoundException {

    }
}
