package service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;

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
}
