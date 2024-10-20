package dataaccess;

import model.AuthData;

/**
 * Memory implementation of AuthDAO
 */
public class MemoryAuthDAO implements AuthDAO{
    @Override
    public void clear() throws DataAccessException {

    }

    @Override
    public void createAuth(AuthData authData) throws DataAccessException {

    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException, EntryNotFoundException {
        return null;
    }

    @Override
    public void deleteAuth(AuthData authData) throws DataAccessException, EntryNotFoundException {

    }
}
