package dataaccess;

import exceptions.DuplicateEntryException;
import exceptions.EntryNotFoundException;
import exceptions.UnauthorizedException;
import model.AuthData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DataAccessTests {

    private static AuthDAO authDAO;
    private static GameDAO gameDAO;
    private static UserDAO userDAO;

    @BeforeAll
    public static void init() {
        authDAO = new DatabaseAuthDAO();
        gameDAO = new DatabaseGameDAO();
        userDAO = new DatabaseUserDAO();
    }

    @BeforeEach
    public void clearDatabase() throws DataAccessException, SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement("TRUNCATE TABLE auth")) {
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement("TRUNCATE TABLE game")) {
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement("TRUNCATE TABLE user")) {
                ps.executeUpdate();
            }
        }
    }

    @Test
    public void addAuthSuccess() throws DataAccessException, SQLException {
        Assertions.assertDoesNotThrow(() -> authDAO.createAuth(new AuthData("abc", "user")));
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement("SELECT authtoken FROM auth WHERE authtoken='abc'")) {
                ResultSet rs = ps.executeQuery();
                Assertions.assertTrue(rs.next());
            }
        }
    }

    @Test
    public void addAuthDuplicate() {
        Assertions.assertDoesNotThrow(() -> authDAO.createAuth(new AuthData("abc", "user")));
        Assertions.assertThrows(DuplicateEntryException.class,
                () -> authDAO.createAuth(new AuthData("abc", "user")));
    }

    @Test
    public void clearAuth() throws DataAccessException, SQLException {
        Assertions.assertDoesNotThrow(() -> authDAO.createAuth(new AuthData("abc", "user")));
        Assertions.assertDoesNotThrow(() -> authDAO.clear());
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement("SELECT authtoken FROM auth")) {
                ResultSet rs = ps.executeQuery();
                Assertions.assertFalse(rs.next());
            }
        }
    }

    @Test
    public void getAuthSuccess() {
        Assertions.assertDoesNotThrow(() -> {
            AuthData testAuth = new AuthData("abc", "user");
            authDAO.createAuth(testAuth);
            Assertions.assertEquals(testAuth, authDAO.getAuth("abc"));
        });
    }

    @Test
    public void getAuthFailure() {
        Assertions.assertThrows(UnauthorizedException.class, () -> authDAO.getAuth("nonexistent"));
    }

    @Test
    public void deleteAuthSuccess() {
        Assertions.assertDoesNotThrow(() -> {
            authDAO.createAuth(new AuthData("abc", "user"));
            authDAO.deleteAuth("abc");
            Assertions.assertThrows(UnauthorizedException.class, () -> authDAO.getAuth("abc"));
        });
    }

    @Test
    public void deleteNonexistentAuth() {
        Assertions.assertThrows(UnauthorizedException.class, () -> authDAO.deleteAuth("nonexistent"));
    }
}
