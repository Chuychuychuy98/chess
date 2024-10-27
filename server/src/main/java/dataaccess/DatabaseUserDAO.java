package dataaccess;

import exceptions.DuplicateEntryException;
import exceptions.EntryNotFoundException;
import model.UserData;

public class DatabaseUserDAO implements UserDAO {
    @Override
    public void clear() throws DataAccessException {

    }

    @Override
    public void createUser(UserData userData) throws DataAccessException, DuplicateEntryException {

    }

    @Override
    public UserData getUser(String username) throws DataAccessException, EntryNotFoundException {
        return null;
    }

    @Override
    public void removeUser(String username) throws DataAccessException, EntryNotFoundException {

    }
}
