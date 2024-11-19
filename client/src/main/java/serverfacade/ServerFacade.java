package serverfacade;

import com.google.gson.Gson;
import exceptions.ResponseException;
import request.LoginRequest;
import request.RegisterRequest;
import result.AuthTokenResult;

import java.io.*;
import java.net.*;

public class ServerFacade {

    private final String serverUrl;
    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public void clear() throws ResponseException {
        this.makeRequest("DELETE", "/db", null, null, null);

    }

    public AuthTokenResult register(String username, String password, String email) throws ResponseException {
        return this.makeRequest("POST", "/user", new RegisterRequest(username, password, email), null, AuthTokenResult.class);
    }

    public AuthTokenResult login(String username, String password) throws ResponseException {
        return this.makeRequest("POST", "/session", new LoginRequest(username, password), null, AuthTokenResult.class);
    }

    public void logout(String authToken) throws ResponseException {
        this.makeRequest("DELETE", "/session", null, authToken, null);
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
            throw new ResponseException(status, "failure: " + status);
        }
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
