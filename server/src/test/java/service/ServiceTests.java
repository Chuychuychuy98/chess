package service;

import dataaccess.*;
import org.junit.jupiter.api.*;
import request.RegisterRequest;


public class ServiceTests {

    private ClearService clearService;
    private UserService userService;

    @BeforeEach
    public void init() {
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        UserDAO userDAO = new MemoryUserDAO();

        clearService = new ClearService(authDAO, gameDAO, userDAO);
        userService = new UserService(userDAO, authDAO);
    }

    @Test
    public void successClear() {
        Assertions.assertDoesNotThrow(() -> clearService.clear());
    }

    @Test
    public void successRegister() {
        Assertions.assertDoesNotThrow(() ->
                userService.register(new RegisterRequest("myName", "myPass", "email@email.org")));
    }

    @Test
    public void failRegister() {
        Assertions.assertThrows(DuplicateEntryException.class, () -> {
            userService.register(new RegisterRequest("myName", "myPass", "email@email.org"));
            userService.register(new RegisterRequest("myName", "myPass", "email@email.org"));
        });
    }
}
