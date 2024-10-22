package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import request.LoginRequest;
import request.RegisterRequest;
import result.AuthTokenResult;

import java.util.UUID;

/**
 * Service class for register, login, and logout endpoints.
 */
public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    /**
     * Service class for register, login, and logout endpoints.
     * @param userDAO UserData DAO to use.
     * @param authDAO AuthData DAO to use.
     */
    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public AuthTokenResult register(RegisterRequest request) throws DuplicateEntryException, DataAccessException, EntryNotFoundException {
        UserData newUser = new UserData(request.username(), request.password(), request.email());
        AuthData newAuth = new AuthData(UUID.randomUUID().toString(), newUser.username());
        userDAO.createUser(newUser);
        try {
            authDAO.createAuth(newAuth);
        }
        catch (Exception e) {
            userDAO.removeUser(newUser.username());
            throw e;
        }
        return new AuthTokenResult(newAuth);
    }

    public AuthTokenResult login(LoginRequest request) throws DataAccessException, UnauthorizedException, DuplicateEntryException {
        UserData user;
        try {
            user = userDAO.getUser(request.username());
        }
        catch (EntryNotFoundException e) {
            throw new UnauthorizedException("Error: unauthorized.");
        }
        if (user.password().equals(request.password())) {
            AuthData newAuth = new AuthData(UUID.randomUUID().toString(), user.username());
            authDAO.createAuth(newAuth);
            return new AuthTokenResult(newAuth);
        }
        throw new UnauthorizedException("Error: unauthorized.");
    }
}
