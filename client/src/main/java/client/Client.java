package client;

import exceptions.ResponseException;
import serverfacade.ServerFacade;
import ui.EscapeSequences;

import java.util.Scanner;

public class Client {
    String authToken = null;
    ServerFacade server;

    public Client(ServerFacade server) {
        this.server = server;
    }

    public void start() {
        System.out.println("♕ Welcome to 240 Chess. Type Help to get started. ♕");
        beforeLogin();
    }

    public void beforeLogin() {
        Scanner in = new Scanner(System.in);
        while (true) {
            System.out.print("[LOGGED_OUT] >>> ");
            String userInput = in.next();
            switch (userInput) {
                case "help":
                    helpBeforeLogin();
                    break;
                case "login":
                    if (login(in)) {
                        afterLogin(in);
                    }
                    else {
                        System.out.println("Failed to log in!");
                    }
                    break;
                case "register":
                    if (register(in)) {
                        afterLogin(in);
                    }
                    else {
                        System.out.println("Failed to register!");
                    }
                    break;
                case "quit":
                    return;
                default:
                    System.out.println("Unrecognized command. For a list of available commands, type \"help\"");
            }
        }
    }

    public void afterLogin(Scanner in) {
        while (true) {
            System.out.print("[LOGGED_IN] >>> ");
            String userInput = in.next();
            switch (userInput) {
                case "help":
                    helpAfterLogin();
                    break;
                case "logout":
                    break;
                case "create":
                    break;
                case "list":
                    break;
                case "join":
                    break;
                case "observe":
                    break;
                default:
                    System.out.println("Unrecognized command. For a list of available commands, type \"help\"");
            }
        }
    }

    public void helpBeforeLogin() {
        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + "login <USERNAME> <PASSWORD>"
                + EscapeSequences.SET_TEXT_COLOR_MAGENTA + " - create an account");
        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + "register <USERNAME> <PASSWORD> <EMAIL>"
                + EscapeSequences.SET_TEXT_COLOR_MAGENTA + " - log into your account to play chess");
        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + "quit"
                + EscapeSequences.SET_TEXT_COLOR_MAGENTA + " - exit the chess client");
        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + "help"
                + EscapeSequences.SET_TEXT_COLOR_MAGENTA + " - print this help message");
        System.out.print(EscapeSequences.RESET_TEXT_COLOR);
    }

    public void helpAfterLogin() {
        System.out.println("You logged in!!!");
    }

    public boolean login(Scanner in) {
        if (in.hasNext()) {
            String username = in.next();
            if (in.hasNext()) {
                String password = in.next();
                try {
                    authToken = server.login(username, password).authToken();
                    return true;
                }
                catch (ResponseException e) {
                    System.out.printf("Error: %s%n", e.getMessage());
                    return false;
                }
            }
            else {
                System.out.println("Error: missing password");
                return false;
            }
        }
        else {
            return loginGetInfo(in);
        }
    }

    public boolean register(Scanner in) {
        if (in.hasNext()) {
            String username = in.next();
            if (in.hasNext()) {
                String password = in.next();
                if (in.hasNext()) {
                    String email = in.next();
                    try {
                        authToken = server.register(username, password, email).authToken();
                        return true;
                    }
                    catch (ResponseException e) {
                        System.out.printf("Error: %s%n", e.getMessage());
                        return false;
                    }
                }
                else {
                    System.out.println("Error: missing email address.");
                    return false;
                }
            }
            else {
                System.out.println("Error: missing password and email address.");
                return false;
            }
        }
        else {
            return registerGetInfo(in);
        }
    }

    public boolean registerGetInfo(Scanner in) {
        System.out.print("Username: ");
        String username = in.next();
        System.out.print("Password: ");
        String password = in.next();
        System.out.print("Email: ");
        String email = in.next();
        try {
            authToken = server.register(username, password, email).authToken();
            return true;
        }
        catch (ResponseException e) {
            System.out.printf("Error: %s%n", e.getMessage());
            return false;
        }
    }

    public boolean loginGetInfo(Scanner in) {
        System.out.print("Username: ");
        String username = in.next();
        System.out.print("Password: ");
        String password = in.next();
        try {
            authToken = server.login(username, password).authToken();
            return true;
        }
        catch (ResponseException e) {
            System.out.printf("Error: %s%n", e.getMessage());
            return false;
        }
    }
}
