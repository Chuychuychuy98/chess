package dataaccess;

import model.UserData;

/**
 * Memory implementation of UserDAO
 */
public class MemoryUserDAO implements UserDAO{
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
}
