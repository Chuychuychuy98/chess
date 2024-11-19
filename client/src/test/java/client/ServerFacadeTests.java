import chess.ChessGame;
import exceptions.ResponseException;
import model.GameData;
import org.junit.jupiter.api.*;
import server.Server;
import serverfacade.ServerFacade;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("localhost:" + port);
    }

    @BeforeEach
    public void clearDatabase() throws ResponseException {
        facade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void registerSuccess() {
        Assertions.assertDoesNotThrow(() -> facade.register("user", "pass", "abc@abc.abc"));
    }

    @Test
    public void registerFailure() {
        ResponseException e = Assertions.assertThrows(ResponseException.class, () -> {
           facade.register("user", "pass", "abc@abc.abc");
           facade.register("user", "pass", "abc@abc.abc");
        });
        Assertions.assertEquals(403, e.getStatus());
    }

    @Test
    public void loginSuccess() {
        Assertions.assertDoesNotThrow(() -> {
            facade.register("user", "pass", "abc@abc.abc");
            facade.login("user", "pass");
        });
    }

    @Test
    public void loginFailure() {
        ResponseException e =
                Assertions.assertThrows(ResponseException.class, () -> facade.login("invalid", "none"));
        Assertions.assertEquals(401, e.getStatus());

    }

    @Test
    public void logoutSuccess() {
        Assertions.assertDoesNotThrow(() -> {
            String authToken = facade.register("user", "pass", "abc@abc.abc").authToken();
            facade.logout(authToken);
        });
    }

    @Test
    public void logoutFailure() {
        ResponseException e =
                Assertions.assertThrows(ResponseException.class, () -> facade.logout("invalid"));
        Assertions.assertEquals(401, e.getStatus());
    }

    @Test
    public void createSuccess() {
        Assertions.assertDoesNotThrow(() -> {
            String authToken = facade.register("user", "pass", "abc@abc.abc").authToken();
            facade.create("new_game", authToken);
        });
    }

    @Test
    public void createFailure() {
        ResponseException e = Assertions.assertThrows(ResponseException.class,
                () -> facade.create("new_game", "invalid"));
        Assertions.assertEquals(401, e.getStatus());
    }

    @Test
    public void listSuccess() {
        Assertions.assertDoesNotThrow(() -> {
            String authToken = facade.register("user", "pass", "abc@abc.abc").authToken();
            facade.create("game1", authToken);
            facade.create("game2", authToken);
            GameData[] games = facade.list(authToken).games();
            Assertions.assertEquals("game1", games[0].gameName());
            Assertions.assertEquals("game2", games[1].gameName());
        });
    }

    @Test
    public void listFailure() {
        ResponseException e =
                Assertions.assertThrows(ResponseException.class, () -> facade.list("invalid"));
        Assertions.assertEquals(401, e.getStatus());
    }

    @Test
    public void joinSuccess() {
        Assertions.assertDoesNotThrow(() -> {
            String authToken = facade.register("user", "pass", "abc@abc.abc").authToken();
            facade.create("game1", authToken);
            GameData[] games = facade.list(authToken).games();
            facade.join(games[0].gameID(), ChessGame.TeamColor.WHITE, authToken);
            games = facade.list(authToken).games();
            Assertions.assertEquals("user", games[0].whiteUsername());
        });
    }

    @Test
    public void joinFailure() {
        ResponseException e = Assertions.assertThrows(ResponseException.class, () -> {
            String authToken = facade.register("user", "pass", "abc@abc.abc").authToken();
            facade.join(23452345, ChessGame.TeamColor.WHITE, authToken);
        });
        Assertions.assertEquals(400, e.getStatus());
    }
}
