package service;

import chess.ChessGame;
import dataaccess.*;
import exceptions.DuplicateEntryException;
import exceptions.EntryNotFoundException;
import exceptions.TeamColorTakenException;
import exceptions.UnauthorizedException;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import request.*;


public class ServiceTests {

    private ClearService clearService;
    private UserService userService;
    private GameService gameService;

    private GameDAO gameDAO;
    private UserDAO userDAO;

    @BeforeEach
    public void init() {
        AuthDAO authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        userDAO = new MemoryUserDAO();

        clearService = new ClearService(authDAO, gameDAO, userDAO);
        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(gameDAO, authDAO);
    }

    @Test
    public void successClear() {
        Assertions.assertDoesNotThrow(() -> clearService.clear());
    }

    @Test
    public void successRegister() {
        Assertions.assertDoesNotThrow(() -> {
                userService.register(new RegisterRequest("myName", "myPass", "email@email.org"));
                Assertions.assertEquals(new UserData("myName", "myPass", "email@email.org"), userDAO.getUser("myName"));
        });
    }

    @Test
    public void failRegister() {
        Assertions.assertThrows(DuplicateEntryException.class, () -> {
            userService.register(new RegisterRequest("myName", "myPass", "email@email.org"));
            userService.register(new RegisterRequest("myName", "myPass", "email@email.org"));
        });
    }

    @Test
    public void successLogin() {
        Assertions.assertDoesNotThrow(() -> {
            userService.register(new RegisterRequest("myName", "myPass", "email@email.org"));
            userService.login(new LoginRequest("myName", "myPass"));
        });
    }

    @Test
    public void loginUserDoesNotExist() {
        Assertions.assertThrows(UnauthorizedException.class, () -> {
            userService.register(new RegisterRequest("myName", "myPass", "email@email.org"));
            userService.login(new LoginRequest("fakeName", "myPass"));
        });
    }

    @Test
    public void loginPasswordDoesNotMatch() {
        Assertions.assertThrows(UnauthorizedException.class, () -> {
            userService.register(new RegisterRequest("myName", "myPass", "email@email.org"));
            userService.login(new LoginRequest("myName", "wrongPass"));
        });
    }

    @Test
    public void successLogoutNoThrow() {
        Assertions.assertDoesNotThrow(() -> {
            String authToken = userService.register(new RegisterRequest("myName", "myPass", "email@email.org")).authToken();
            userService.logout(new AuthTokenRequest(authToken));
        });
    }

    @Test
    public void logoutActuallyDeleted() {
        Assertions.assertThrows(UnauthorizedException.class, () -> {
            String authToken = userService.register(new RegisterRequest("myName", "myPass", "email@email.org")).authToken();
            userService.logout(new AuthTokenRequest(authToken));
            userService.logout(new AuthTokenRequest(authToken));
        });
    }

    @Test
    public void logoutNonExistent() {
        Assertions.assertThrows(UnauthorizedException.class, () -> userService.logout(new AuthTokenRequest("abc")));
    }

    @Test
    public void successListEmpty() {
        Assertions.assertDoesNotThrow(() -> {
            String authToken = userService.register(new RegisterRequest("myName", "myPass", "email@email.org")).authToken();
            gameService.list(new AuthTokenRequest(authToken));
        });
    }

    @Test
    public void successListWithGames() {
        Assertions.assertDoesNotThrow(() -> {
            String authToken = userService.register(new RegisterRequest("myName", "myPass", "email@email.org")).authToken();
            gameService.create(new CreateGameRequest("game1"), new AuthTokenRequest(authToken));
            gameService.create(new CreateGameRequest("game2"), new AuthTokenRequest(authToken));
            GameData[] games = gameService.list(new AuthTokenRequest(authToken)).games();
            Assertions.assertEquals("game1", games[0].gameName());
            Assertions.assertEquals("game2", games[1].gameName());
        });
    }

    @Test
    public void failList() {
        Assertions.assertThrows(UnauthorizedException.class, () -> gameService.list(new AuthTokenRequest("abc")));
    }

    @Test
    public void successCreateGame() {
        Assertions.assertDoesNotThrow(() -> {
            String authToken = userService.register(new RegisterRequest("myName", "myPass", "email@email.org")).authToken();
            int id = gameService.create(new CreateGameRequest("game"), new AuthTokenRequest(authToken)).gameID();
            Assertions.assertEquals("game", gameDAO.getGame(id).gameName());
        });
    }

    @Test
    public void createGameUnauthorized() {
        Assertions.assertThrows(UnauthorizedException.class, () ->
                gameService.create(new CreateGameRequest("game"), new AuthTokenRequest("abc")));
    }

    @Test
    public void joinGameSuccess() {
        Assertions.assertDoesNotThrow(() -> {
            String authToken1 = userService.register(new RegisterRequest("myName", "myPass", "email@email.org")).authToken();
            String authToken2 = userService.register(new RegisterRequest("second", "two", "email@email.org")).authToken();
            int id = gameService.create(new CreateGameRequest("game"), new AuthTokenRequest(authToken1)).gameID();
            gameService.join(new JoinRequest(ChessGame.TeamColor.BLACK, id), new AuthTokenRequest(authToken1));
            gameService.join(new JoinRequest(ChessGame.TeamColor.WHITE, id), new AuthTokenRequest(authToken2));
            GameData game = gameDAO.getGame(id);
            Assertions.assertEquals("myName", game.blackUsername());
            Assertions.assertEquals("second", game.whiteUsername());
        });
    }

    @Test
    public void joinNonexistentGame() {
        Assertions.assertThrows(EntryNotFoundException.class, () -> {
            String authToken = userService.register(new RegisterRequest("myName", "myPass", "email@email.org")).authToken();
            gameService.join(new JoinRequest(ChessGame.TeamColor.BLACK, 100), new AuthTokenRequest(authToken));
        });
    }

    @Test
    public void joinGameUnauthorized() {
        Assertions.assertThrows(UnauthorizedException.class, () ->
                gameService.join(new JoinRequest(ChessGame.TeamColor.BLACK, 100), new AuthTokenRequest("asdf")));
    }

    @Test
    public void joinGameColorTaken() {
        Assertions.assertThrows(TeamColorTakenException.class, () -> {
            String authToken1 = userService.register(new RegisterRequest("myName", "myPass", "email@email.org")).authToken();
            String authToken2 = userService.register(new RegisterRequest("second", "two", "email@email.org")).authToken();
            int id = gameService.create(new CreateGameRequest("game"), new AuthTokenRequest(authToken1)).gameID();
            gameService.join(new JoinRequest(ChessGame.TeamColor.BLACK, id), new AuthTokenRequest(authToken1));
            gameService.join(new JoinRequest(ChessGame.TeamColor.BLACK, id), new AuthTokenRequest(authToken2));
        });
    }
}
