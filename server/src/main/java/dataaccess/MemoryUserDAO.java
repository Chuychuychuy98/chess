package dataaccess;

import model.UserData;

import java.util.HashMap;
import java.util.Map;

/**
 * Memory implementation of UserDAO
 */
public class MemoryUserDAO implements UserDAO{

    Map<String, UserData> database = new HashMap<>();

    @Override
    public void clear() {
        database.clear();
    }

    @Override
    public void createUser(UserData userData) throws DuplicateEntryException {
        if (database.get(userData.username()) == null) {
            throw new DuplicateEntryException(String.format("Username %s is taken.", userData.username()));
        }
        database.put(userData.username(), userData);
    }

    @Override
    public UserData getUser(String username) throws EntryNotFoundException {
        UserData data = database.get(username);
        if (data == null) throw new EntryNotFoundException(String.format("No user found with username %s.", username));
        return data;
    }
}
