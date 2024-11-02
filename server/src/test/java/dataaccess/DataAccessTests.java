package dataaccess;

import chess.ChessGame;
import exceptions.DuplicateEntryException;
import exceptions.EntryNotFoundException;
import exceptions.UnauthorizedException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

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

    @Test
    public void checkAuthSuccess() {
        Assertions.assertDoesNotThrow(() -> {
            authDAO.createAuth(new AuthData("abc", "user"));
            authDAO.checkAuth("abc");
        });
    }

    @Test
    public void checkAuthFailure() {
        Assertions.assertThrows(UnauthorizedException.class, () -> authDAO.checkAuth("nonexistent"));
    }

    @Test
    public void createUserSuccess() {
        Assertions.assertDoesNotThrow(() -> {
            UserData data = new UserData("myname", "mypass", "email@email.com");
            userDAO.createUser(data);
            try (Connection conn = DatabaseManager.getConnection()) {
                try (PreparedStatement ps = conn.prepareStatement("SELECT username, password, email FROM user WHERE username=?")) {
                    ps.setString(1, "myname");
                    ResultSet rs = ps.executeQuery();
                    Assertions.assertTrue(rs.next());
                    Assertions.assertEquals(data,
                            new UserData(rs.getString("username"),
                                    rs.getString("password"),
                                    rs.getString("email")));
                }
            }
        });
    }

    @Test
    public void createUserDuplicate() {
        UserData data = new UserData("user", "pass", "email@email.com");
        Assertions.assertDoesNotThrow(() -> userDAO.createUser(data));
        Assertions.assertThrows(DuplicateEntryException.class, () -> userDAO.createUser(data));
    }

    @Test
    public void getUserSuccess() {
        UserData data = new UserData("user", "pass", "email@email.com");
        Assertions.assertDoesNotThrow(() -> {
            userDAO.createUser(data);
            Assertions.assertEquals(data, userDAO.getUser("user"));
        });
    }

    @Test
    public void getUserNonexistent() {
        Assertions.assertThrows(EntryNotFoundException.class, () -> userDAO.getUser("nobody"));
    }

    @Test
    public void clearUser() {
        Assertions.assertDoesNotThrow(() -> {
           userDAO.createUser(new UserData("user", "pass", "email@email.com"));
           userDAO.createUser(new UserData("user2", "pass2", "email2@email.com"));
           userDAO.clear();
        });
        Assertions.assertThrows(EntryNotFoundException.class, () -> userDAO.getUser("user"));
        Assertions.assertThrows(EntryNotFoundException.class, () -> userDAO.getUser("user2"));
    }

    @Test
    public void deleteUserSuccess() {
        Assertions.assertDoesNotThrow(() -> {
            userDAO.createUser(new UserData("user", "pass", "email@email.com"));
            userDAO.createUser(new UserData("user2", "pass2", "email2@email.com"));
            userDAO.removeUser("user");
            userDAO.getUser("user2");
        });
        Assertions.assertThrows(EntryNotFoundException.class, () -> userDAO.getUser("user"));
    }

    @Test
    public void createGameSuccess() {
        Assertions.assertDoesNotThrow(() -> {
            GameData gameData = new GameData(1, "white", "black", "name", new ChessGame());
            gameDAO.createGame(gameData);
            try (Connection conn = DatabaseManager.getConnection()) {
                try (PreparedStatement ps = conn.prepareStatement("SELECT gameID, whiteUsername, blackUsername, gameName, game FROM game WHERE gameID=?")) {
                    ps.setInt(1, 1);
                    ResultSet rs = ps.executeQuery();
                    Assertions.assertTrue(rs.next());

                    Assertions.assertEquals(gameData,
                            new GameData(rs.getInt("gameID"),
                                    rs.getString("whiteUsername"),
                                    rs.getString("blackUsername"),
                                    rs.getString("gameName"),
                                    rs.getString("game")));
                }
            }
        });
    }

    @Test
    public void createGameDuplicateID() {
        Assertions.assertThrows(DuplicateEntryException.class, () -> {
            gameDAO.createGame(new GameData(1, "white", "black", "name", new ChessGame()));
            gameDAO.createGame(new GameData(1, "white", "black", "name", new ChessGame()));
        });
    }

    @Test
    public void clearGame() throws DataAccessException, SQLException {
        Assertions.assertDoesNotThrow(() -> {
            gameDAO.createGame(new GameData(1, "white", "black", "name", new ChessGame()));
            gameDAO.clear();
        });
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement("SELECT gameID FROM game")) {
                ResultSet rs = ps.executeQuery();
                Assertions.assertFalse(rs.next());
            }
        }
    }

    @Test
    public void getGameSuccess() {
        Assertions.assertDoesNotThrow(() -> {
            GameData data = new GameData(1, "white", "black", "name", new ChessGame());
            gameDAO.createGame(data);
            Assertions.assertEquals(data, gameDAO.getGame(1));
        });
    }

    @Test
    public void getNonexistentGame() {
        Assertions.assertThrows(EntryNotFoundException.class, () -> gameDAO.getGame(12345678));
    }

    @Test
    public void listAllGames() {
        Assertions.assertDoesNotThrow(() -> {
            GameData[] games = {
                    new GameData(1, "white", "black", "game", new ChessGame()),
                    new GameData(2, "white", "black", "game", new ChessGame()),
                    new GameData(3, "white", "black", "game", new ChessGame())
            };
            for (GameData data : games) {
                gameDAO.createGame(data);
            }
            GameData[] fromDatabase = gameDAO.listGames();
            for (int i = 0; i < games.length; i++) {
                Assertions.assertEquals(games[i], fromDatabase[i]);
            }
        });
    }

    @Test
    public void listAllGamesEmpty() {
        Assertions.assertDoesNotThrow(() -> {
            Assertions.assertEquals(0, gameDAO.listGames().length);
        });
    }
}
