package dataaccess;

import model.AuthData;

import java.util.HashSet;
import java.util.Set;

/**
 * Memory implementation of AuthDAO
 */
public class MemoryAuthDAO implements AuthDAO{

    Set<AuthData> database = new HashSet<>();

    @Override
    public void clear() {
        database.clear();
    }

    @Override
    public void createAuth(AuthData authData) {

    }

    @Override
    public AuthData getAuth(String authToken) throws EntryNotFoundException {
        return null;
    }

    @Override
    public void deleteAuth(AuthData authData) throws EntryNotFoundException {

    }
}
