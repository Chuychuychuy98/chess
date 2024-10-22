package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.Map;

/**
 * Memory implementation of AuthDAO
 */
public class MemoryAuthDAO implements AuthDAO{

    Map<String, AuthData> database = new HashMap<>();

    @Override
    public void clear() {
        database.clear();
    }

    @Override
    public void createAuth(AuthData authData) throws DuplicateEntryException {
        if (database.containsKey(authData.authToken())) {
            throw new DuplicateEntryException("Error: authToken already in use");
        }
        database.put(authData.authToken(), authData);
    }

    @Override
    public AuthData getAuth(String authToken) throws UnauthorizedException {
        AuthData data =  database.get(authToken);
        if (data == null) throw new UnauthorizedException("Error: unauthorized");
        return data;
    }

    @Override
    public void deleteAuth(String authToken) throws UnauthorizedException {
        AuthData data = database.remove(authToken);
        if (data == null) throw new UnauthorizedException("Error: unauthorized");
    }
}
