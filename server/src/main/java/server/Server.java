package server;

import com.google.gson.Gson;
import dataaccess.*;
import exceptions.*;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import request.*;
import service.ClearService;
import service.GameService;
import service.UserService;
import spark.*;

@WebSocket
public class Server {

    private final ClearService clearService;
    private final GameService gameService;
    private final UserService userService;

    String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS auth (
              authtoken CHAR(36) NOT NULL,
              username VARCHAR(256) NOT NULL,
              PRIMARY KEY (authtoken)
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS game (
              gameID int AUTO_INCREMENT NOT NULL,
              whiteUsername VARCHAR(256),
              blackUsername VARCHAR(256),
              gameName VARCHAR(256) NOT NULL,
              game VARCHAR(4096) NOT NULL,
              PRIMARY KEY (gameID)
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS user (
              username VARCHAR(256) NOT NULL,
              password VARCHAR(256) NOT NULL,
              email VARCHAR(256) NOT NULL,
              PRIMARY KEY (username)
            )
            """
    };

    public Server() {
        try {
            DatabaseManager.configureDatabase(createStatements);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        AuthDAO authDAO = new DatabaseAuthDAO();
        GameDAO gameDAO = new DatabaseGameDAO();
        UserDAO userDAO = new DatabaseUserDAO();


        clearService = new ClearService(authDAO, gameDAO, userDAO);
        gameService = new GameService(gameDAO, authDAO);
        userService = new UserService(userDAO, authDAO);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.webSocket("ws", Server.class);
        Spark.delete("/db", this::clear);
        Spark.post("/user", this::register);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.get("/game", this::list);
        Spark.post("/game", this::create);
        Spark.put("/game", this::join);

        Spark.exception(DataAccessException.class, this::otherExceptionHandler);
        Spark.exception(EntryNotFoundException.class, this::otherExceptionHandler);
        Spark.exception(DuplicateEntryException.class, this::duplicateExceptionHandler);
        Spark.exception(TeamColorTakenException.class, this::duplicateExceptionHandler);
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

    @OnWebSocketMessage
    public void onMessage(Session session, String msg) throws Exception {
        System.out.printf("Received: %s", msg);
        session.getRemote().sendString("Websocket response: " + msg);
        /*
        try {
            UserGameCommand cmd = Serializer.fromJson(msg, UserGameCommand.class);

            String username = getUsername(cmd.getAuthString());

            saveSession(cmd.getGameId(), session);
            switch (cmd.getCommandType()) {
                case CONNECT -> connect(session, username, (ConnectCommand)cmd);
                case MAKE_MOVE -> makeMove(session, username, (MakeMoveCommand)cmd);
                case LEAVE -> leave(session, username, (LeaveCommand)cmd);
                case RESIGN -> resign(session, username, (ResignCommand)cmd);
            }
        }
        catch (UnauthorizedException e) {
            sendMessage(session.getRemote(), new ErrorMessage("Error: unauthorized");
        }
        catch (Exception e) {
            e.printStackTrace();
            sendMessage(session.getRemote(), new ErrorMessage("Error: " + e.getMessage());
        }
         */
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

    private void duplicateExceptionHandler(Exception ex, Request req, Response res) {
        res.status(403);
        res.body(formatError(ex.getMessage()));
    }

    private Object clear(Request req, Response res) throws DataAccessException {
        clearService.clear();
        res.status(200);
        return "";
    }

    private Object register(Request req, Response res)
            throws DataAccessException, EntryNotFoundException, DuplicateEntryException, BadRequestException {
        RegisterRequest regReq = new Gson().fromJson(req.body(), RegisterRequest.class);
        if (regReq.username() == null || regReq.password() == null || regReq.email() == null) {
            throw new BadRequestException("Error: bad request");
        }
        return new Gson().toJson(userService.register(regReq));
    }

    private Object login(Request req, Response res)
            throws BadRequestException, UnauthorizedException, DuplicateEntryException, DataAccessException {
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

    private Object create(Request req, Response res)
            throws UnauthorizedException, DuplicateEntryException, DataAccessException, BadRequestException {
        String authToken = req.headers("authorization");
        checkAuthToken(authToken);
        CreateGameRequest createGameRequest = new Gson().fromJson(req.body(), CreateGameRequest.class);
        if (createGameRequest.gameName() == null) {
            throw new BadRequestException("Error: bad request");
        }
        return new Gson().toJson(gameService.create(createGameRequest, new AuthTokenRequest(authToken)));
    }

    private Object join(Request req, Response res)
            throws BadRequestException, UnauthorizedException, TeamColorTakenException, DataAccessException {
        String authToken = req.headers("authorization");
        checkAuthToken(authToken);
        JoinRequest joinRequest = new Gson().fromJson(req.body(), JoinRequest.class);
        if (joinRequest.playerColor() == null) {
            throw new BadRequestException("Error: no team color set");
        }
        try {
            gameService.join(joinRequest, new AuthTokenRequest(authToken));
        } catch (EntryNotFoundException ex) {
            throw new BadRequestException("Error: no game with that ID exists");
        }
        return "";
    }

    private void checkAuthToken(String authToken) throws UnauthorizedException {
        if (authToken == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }
    }
}
