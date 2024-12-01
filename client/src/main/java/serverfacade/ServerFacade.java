package serverfacade;

import chess.ChessGame;
import chess.ChessMove;
import client.Client;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import exceptions.ResponseException;
import request.CreateGameRequest;
import request.JoinRequest;
import request.LoginRequest;
import request.RegisterRequest;
import result.AuthTokenResult;
import result.ListResult;
import websocket.commands.ConnectCommand;
import websocket.commands.LeaveCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.*;
import java.net.*;

public class ServerFacade extends Endpoint {

    private final String serverUrl;
    private Session session = null;
    private final Client observer;
    private final Gson serializer;

    public ServerFacade(String serverUrl, Client observer) {
        this.serverUrl = serverUrl;
        this.observer = observer;

        this.serializer = new GsonBuilder().registerTypeAdapter(ServerMessage.class,
                (JsonDeserializer<ServerMessage>) (el, type, ctx) -> {
                    ServerMessage msg = null;
                    if (el.isJsonObject()) {
                        String msgType = el.getAsJsonObject().get("type").getAsString();
                        msg = switch (ServerMessage.ServerMessageType.valueOf(msgType)) {
                            case NOTIFICATION -> ctx.deserialize(el, NotificationMessage.class);
                            case ERROR -> ctx.deserialize(el, ErrorMessage.class);
                            case LOAD_GAME -> ctx.deserialize(el, LoadGameMessage.class);
                        };
                    }
                    return msg;
                }).create();
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }

    private void webSocketConnect() throws URISyntaxException, DeploymentException, IOException {
        URI uri = new URI("ws://" + serverUrl + "/ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);
        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String msg) {
                try {
                    ServerMessage serverMsg = serializer.fromJson(msg, ServerMessage.class);
                    observer.notify(serverMsg);
                } catch (Exception e) {
                    observer.notify(new ErrorMessage(e.getMessage()));
                }
            }
        });
    }

    public void observe(String authToken, int id) {
        try {
            if (session == null) {
                webSocketConnect();
            }
            send(new ConnectCommand(authToken, id));
        } catch (Exception e) {
            observer.notify(new ErrorMessage(e.getMessage()));
        }
    }

    public boolean leave(String authToken, int id) {
        try {
            send(new LeaveCommand(authToken, id));
            return true;
        }
        catch (Exception e) {
            observer.notify(new ErrorMessage(e.getMessage()));
            return false;
        }
    }

    public void move(String authToken, int id, ChessMove move) {
        try {
            send(new MakeMoveCommand(authToken, id, move));
        }
        catch (Exception e) {
            observer.notify(new ErrorMessage(e.getMessage()));
        }
    }

    public void send(UserGameCommand msg) throws IOException {
        this.session.getBasicRemote().sendText(new Gson().toJson(msg));
    }

    public void clear() throws ResponseException {
        this.makeRequest("DELETE", "/db", null, null, null);

    }

    public AuthTokenResult register(String username, String password, String email) throws ResponseException {
        return this.makeRequest("POST", "/user",
                new RegisterRequest(username, password, email), null, AuthTokenResult.class);
    }

    public AuthTokenResult login(String username, String password) throws ResponseException {
        return this.makeRequest("POST", "/session",
                new LoginRequest(username, password), null, AuthTokenResult.class);
    }

    public void logout(String authToken) throws ResponseException {
        this.makeRequest("DELETE", "/session", null, authToken, null);
    }

    public void create(String gameName, String authToken) throws ResponseException {
        this.makeRequest("POST", "/game", new CreateGameRequest(gameName), authToken, null);
    }

    public ListResult list(String authToken) throws ResponseException {
        return this.makeRequest("GET", "/game", null, authToken, ListResult.class);
    }

    public void join(int gameID, ChessGame.TeamColor team, String authToken) throws ResponseException {
        this.makeRequest("PUT", "/game", new JoinRequest(team, gameID), authToken, null);
        this.observe(authToken, gameID);
    }

    private <T> T makeRequest(String method, String path, Object request, String authToken, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI("http://" + serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if (authToken != null) {
                http.setRequestProperty("Authorization", authToken);
            }

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        }
        catch (ResponseException e) {
            throw e;
        }
        catch (Exception e) {
            throw new ResponseException(500, e.getMessage());
        }
    }


    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            if (status == 400) {
                throw new ResponseException(status, "Malformed request.");
            }
            if (status == 401) {
                throw new ResponseException(status, "Unauthorized.");
            }
            if (status == 403) {
                throw new ResponseException(status, "Already taken.");
            }
            throw new ResponseException(status, "failure: " + status);
        }
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
