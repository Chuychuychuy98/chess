package dataaccess;

import exceptions.DuplicateEntryException;
import exceptions.UnauthorizedException;
import model.AuthData;

public class DatabaseAuthDAO implements AuthDAO {
    @Override
    public void clear() throws DataAccessException {

    }

    @Override
    public void createAuth(AuthData authData) throws DataAccessException, DuplicateEntryException {

    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException, UnauthorizedException {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException, UnauthorizedException {

    }

    @Override
    public void checkAuth(String authToken) throws DataAccessException, UnauthorizedException {

    }
}
