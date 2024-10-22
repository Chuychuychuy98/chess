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
            throw new DuplicateEntryException("That authToken is already in use.");
        }
        database.put(authData.authToken(), authData);
    }

    @Override
    public AuthData getAuth(String authToken) throws EntryNotFoundException {
        AuthData data =  database.get(authToken);
        if (data == null) throw new EntryNotFoundException("User is not authorized.");
        return data;
    }

    @Override
    public void deleteAuth(AuthData authData) throws EntryNotFoundException {
        AuthData data = database.remove(authData.authToken());
        if (data == null) throw new EntryNotFoundException("User is not authorized.");
    }
}
