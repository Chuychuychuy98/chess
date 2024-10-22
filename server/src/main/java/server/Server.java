package server;

import com.google.gson.Gson;
import dataaccess.*;
import exceptions.BadRequestException;
import exceptions.DuplicateEntryException;
import exceptions.EntryNotFoundException;
import exceptions.UnauthorizedException;
import request.AuthTokenRequest;
import request.LoginRequest;
import request.RegisterRequest;
import service.ClearService;
import service.GameService;
import service.UserService;
import spark.*;

public class Server {

    private final ClearService clearService;
    private final GameService gameService;
    private final UserService userService;

    public Server() {
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        UserDAO userDAO = new MemoryUserDAO();

        clearService = new ClearService(authDAO, gameDAO, userDAO);
        gameService = new GameService(gameDAO, authDAO);
        userService = new UserService(userDAO, authDAO);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", this::clear);
        Spark.post("/user", this::register);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.get("/game", this::list);

        Spark.exception(DataAccessException.class, this::otherExceptionHandler);
        Spark.exception(EntryNotFoundException.class, this::otherExceptionHandler);
        Spark.exception(DuplicateEntryException.class, this::duplicateEntryExceptionHandler);
        Spark.exception(BadRequestException.class, this::badRequestExceptionHandler);
        Spark.exception(UnauthorizedException.class, this::unauthorizedException);
        //This line initializes the server and can be removed once you have a functioning endpoint
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private String formatError(String msg) {
        return "{\"message\":\"" + msg + "\"}";
    }

    private void otherExceptionHandler(Exception ex, Request req, Response res) {
        res.status(500);
        res.body(formatError(ex.getMessage()));
    }

    private void badRequestExceptionHandler(BadRequestException ex, Request req, Response res) {
        res.status(400);
        res.body(formatError(ex.getMessage()));
    }

    private void unauthorizedException(UnauthorizedException ex, Request req, Response res) {
        res.status(401);
        res.body(formatError(ex.getMessage()));
    }

    private void duplicateEntryExceptionHandler(DuplicateEntryException ex, Request req, Response res) {
        res.status(403);
        res.body(formatError(ex.getMessage()));
    }

    private Object clear(Request req, Response res) throws DataAccessException {
        clearService.clear();
        res.status(200);
        return "";
    }

    private Object register(Request req, Response res) throws DataAccessException, EntryNotFoundException, DuplicateEntryException, BadRequestException {
        RegisterRequest regReq = new Gson().fromJson(req.body(), RegisterRequest.class);
        if (regReq.username() == null || regReq.password() == null || regReq.email() == null) {
            throw new BadRequestException("Error: bad request");
        }
        return new Gson().toJson(userService.register(regReq));
    }

    private Object login(Request req, Response res) throws BadRequestException, UnauthorizedException, DuplicateEntryException, DataAccessException {
        LoginRequest logReq = new Gson().fromJson(req.body(), LoginRequest.class);
        if (logReq.username() == null || logReq.password() == null) {
            throw new BadRequestException("Error: bad request");
        }
        return new Gson().toJson(userService.login(logReq));
    }

    private Object logout(Request req, Response res) throws UnauthorizedException, DataAccessException {
        String authToken = req.headers("authorization");
        checkAuthToken(authToken);
        userService.logout(new AuthTokenRequest(authToken));
        return "";
    }

    private Object list(Request req, Response res) throws UnauthorizedException, DataAccessException {
        String authToken = req.headers("authorization");
        checkAuthToken(authToken);
        return new Gson().toJson(gameService.list(new AuthTokenRequest(authToken)));
    }

}
