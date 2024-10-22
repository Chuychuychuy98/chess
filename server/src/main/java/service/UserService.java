package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import request.LoginRequest;
import request.AuthTokenRequest;
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

    /**
     * Register a new user.
     * @param request The request containing the username, password, and email to register.
     * @return AuthTokenResult containing the new authToken and the username associated with it.
     * @throws DuplicateEntryException Indicates that the username was already taken or that the random authToken was not unique.
     * @throws DataAccessException Indicates an error reaching the database.
     * @throws EntryNotFoundException Indicates that the user was added, but adding the authToken failed, then the user could not be found when trying to remove it.
     */
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

    /**
     * Log a user in and associate it with a new authToken.
     * @param request The request containing the username and password to log in.
     * @return AuthTokenResult containing the new authToken and the username associated with it.
     * @throws DataAccessException Indicates an error reaching the database.
     * @throws UnauthorizedException Indicates that the username does not exist, or the username and password do not match.
     * @throws DuplicateEntryException Indicates that the generated authToken was not unique.
     */
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

    /**
     * Log a user out by deleting their authToken if it exists.
     * @param request LogoutRequest containing the authToken to delete.
     * @throws DataAccessException Indicates an error reaching the database.
     * @throws UnauthorizedException Indicates that the authToken is not in the database.
     */
    public void logout(AuthTokenRequest request) throws DataAccessException, UnauthorizedException {
        authDAO.deleteAuth(request.authToken());
    }
}
