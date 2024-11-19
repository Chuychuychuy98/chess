import exceptions.ResponseException;
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
        Assertions.assertThrows(ResponseException.class, () -> {
           facade.register("user", "pass", "abc@abc.abc");
           facade.register("user", "pass", "abc@abc.abc");
        });
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
        Assertions.assertThrows(ResponseException.class, () -> facade.login("invalid", "none"));
    }
}
