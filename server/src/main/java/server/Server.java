package server;

import chess.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import dataaccess.*;
import exceptions.*;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import request.*;
import service.ClearService;
import service.GameService;
import service.UserService;
import spark.*;
import websocket.commands.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;

@WebSocket
public class Server {

    private final ConnectionManager connections = new ConnectionManager();
    private final Gson serializer;

    private final ClearService clearService;
    private final GameService gameService;
    private final UserService userService;

    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final UserDAO userDAO;

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
        authDAO = new DatabaseAuthDAO();
        gameDAO = new DatabaseGameDAO();
        userDAO = new DatabaseUserDAO();


        clearService = new ClearService(authDAO, gameDAO, userDAO);
        gameService = new GameService(gameDAO, authDAO);
        userService = new UserService(userDAO, authDAO);

        this.serializer = new GsonBuilder().registerTypeAdapter(UserGameCommand.class,
                (JsonDeserializer<UserGameCommand>) (el, type, ctx) -> {
                    UserGameCommand cmd = null;
                    if (el.isJsonObject()) {
                        String msgType = el.getAsJsonObject().get("commandType").getAsString();
                        cmd = switch (UserGameCommand.CommandType.valueOf(msgType)) {
                            case CONNECT -> ctx.deserialize(el, ConnectCommand.class);
                            case MAKE_MOVE -> ctx.deserialize(el, MakeMoveCommand.class);
                            case LEAVE -> ctx.deserialize(el, LeaveCommand.class);
                            case RESIGN -> ctx.deserialize(el, ResignCommand.class);
                        };
                    }
                    return cmd;
                }).create();
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.webSocket("/ws", Server.class);
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
    public void onMessage(Session session, String msg) {
        try {
            UserGameCommand cmd = serializer.fromJson(msg, UserGameCommand.class);

            String username = authDAO.getAuth(cmd.getAuthToken()).username();
            int gameID = cmd.getGameID();
            saveSession(gameID, username, session);
            switch (cmd.getCommandType()) {
                case CONNECT -> connect(session, username, gameID, ((ConnectCommand)cmd).getColor());
                case MAKE_MOVE -> makeMove(session, username, gameID, ((MakeMoveCommand)cmd).getMove());
                case LEAVE -> leave(session, username, gameID);
                case RESIGN -> resign(session, username, gameID);
            }
        }
        catch (UnauthorizedException e) {
            send(session, new ErrorMessage("unauthorized"));
        }
        catch (Exception e) {
            e.printStackTrace();
            send(session, new ErrorMessage(e.getMessage()));
        }
    }

    private void connect(Session session, String username, int gameID, ChessGame.TeamColor color)
            throws IOException, DataAccessException {
        try {
            send(session, new LoadGameMessage(new Gson().toJson(gameDAO.getGame(gameID))));
            if (color == null) {
                connections.broadcast(gameID, new NotificationMessage(username + " is now observing!"), username);

            }
            else {
                connections.broadcast(gameID,
                        new NotificationMessage(username + " has joined as " + color + "!"), username);
            }
        } catch (EntryNotFoundException e) {
            send(session, new ErrorMessage("No game with ID " + gameID + " exists."));
        }
    }

    private void leave(Session session, String username, int gameID) throws IOException, DataAccessException {
        try {
            gameDAO.playerLeave(gameID, username);
            connections.remove(username);
            connections.broadcast(gameID, new NotificationMessage(username + " has left the match."), username);
        }
        catch (EntryNotFoundException e) {
            send(session, new ErrorMessage("No game with ID " + gameID + " exists."));
        }
    }

    private void makeMove(Session session, String username, int gameID, ChessMove move) throws IOException, DataAccessException {
        try {
            GameData game = gameDAO.getGame(gameID);
            ChessGame.TeamColor color;
            String opponentName;
            if (game.whiteUsername().equals(username)) {
                color = ChessGame.TeamColor.WHITE;
                opponentName = game.blackUsername();
            }
            else  {
                color = ChessGame.TeamColor.BLACK;
                opponentName = game.whiteUsername();
            }
            gameDAO.makeMove(gameID, username, move);
            game = gameDAO.getGame(gameID);

            connections.broadcast(gameID, new LoadGameMessage(new Gson().toJson(game)));
            connections.broadcast(gameID, new NotificationMessage(username + " moved " +
                    move.getStartPosition().chessNotation() + " to " +
                    move.getEndPosition().chessNotation() + "."), username);
            if (game.game().isInCheckmate(color.opposite())) {
                connections.broadcast(gameID, new NotificationMessage(opponentName + " is now in checkmate!\nGAME OVER!"));
            }
            else if (game.game().isInCheck(color.opposite())) {
                connections.broadcast(gameID, new NotificationMessage(opponentName + " is now in checkmate!\nGAME OVER!"));
            }
            else if (game.game().isInStalemate(color.opposite())) {
                connections.broadcast(gameID, new NotificationMessage("Stalemate!\nGAME OVER!"));
            }
        } catch (EntryNotFoundException e) {
            send(session, new ErrorMessage("No game with ID " + gameID + " exists."));
        } catch (GameOverException e){
            send(session, new ErrorMessage(e.getMessage()));
        } catch (WrongTurnException e) {
            send(session, new ErrorMessage("It is not your turn."));
        } catch (NonexistentPlayerException e) {
            send(session, new ErrorMessage("You are not currently playing this game. To join a game, type " +
                    "\u001b[38;512mleave\u001b[38;5160m and join a new game"));
        } catch (InvalidMoveException e) {
            send(session, new ErrorMessage(e.getMessage() + "For a list of valid moves, try typing " +
                    "\u001b[38;512mmoves\u001b[38;5160m"));
        }
    }

    private void resign(Session session, String username, int gameID) throws IOException, DataAccessException {
        try {
            GameData data = gameDAO.getGame(gameID);
            if (!username.equals(data.whiteUsername()) && !username.equals(data.blackUsername())) {
                send(session, new ErrorMessage("You are not currently playing this game. To join a game, type " +
                    "\u001b[38;512mleave\u001b[38;5160m and join a new game"));
                return;
            }
            gameDAO.setGameOver(gameID);
            connections.broadcast(gameID, new NotificationMessage(username + " has resigned!\nGAME OVER!"));
        }
        catch (EntryNotFoundException e) {
            send(session, new ErrorMessage("No game with ID " + gameID + " exists."));
        }
        catch (GameOverException e) {
            send(session, new ErrorMessage(e.getMessage()));
        }
    }

    private void saveSession(int gameID, String username, Session session) {
        connections.add(gameID, username, session);
    }

    private void send(Session session, ServerMessage msg) {
        try {
            session.getRemote().sendString(new Gson().toJson(msg));
        } catch (IOException e) {
            System.out.printf("Could not send message to client.%nMessage: %s%n", new Gson().toJson(msg));
        }
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
