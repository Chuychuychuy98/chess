package service;

import dataaccess.*;
import org.junit.jupiter.api.*;


public class ServiceTests {

    private static ClearService clearService;

    @BeforeAll
    public static void init() {
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        UserDAO userDAO = new MemoryUserDAO();

        clearService = new ClearService(authDAO, gameDAO, userDAO);
    }

    @Test
    public void successClear() {
        Assertions.assertDoesNotThrow(() -> clearService.clear());
    }

}
