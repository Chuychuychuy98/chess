package client;

import exceptions.ResponseException;
import model.GameData;
import serverfacade.ServerFacade;
import ui.EscapeSequences;

import java.util.Scanner;

public class Client {
    String authToken = null;
    String username = null;
    ServerFacade server;

    public Client(ServerFacade server) {
        this.server = server;
    }

    public void start() {
        System.out.println("♕ Welcome to 240 Chess. Type help to get started. ♕");
        beforeLogin();
    }

    private void beforeLogin() {
        Scanner in = new Scanner(System.in);
        while (true) {
            System.out.print("[LOGGED_OUT] >>> ");
            String userInput = in.next().toLowerCase();
            switch (userInput) {
                case "help":
                    helpBeforeLogin();
                    break;
                case "login":
                    if (login(in)) {
                        afterLogin(in);
                    }
                    else {
                        printError("Failed to log in");
                    }
                    break;
                case "register":
                    if (register(in)) {
                        afterLogin(in);
                    }
                    else {
                        printError("Failed to register");
                    }
                    break;
                case "quit":
                    return;
                default:
                    System.out.println("Unrecognized command. For a list of available commands, type \"help\"");
            }
        }
    }

    private void afterLogin(Scanner in) {
        while (true) {
            System.out.printf("[%s] >>> ", username);
            String userInput = in.next().toLowerCase();
            switch (userInput) {
                case "help":
                    helpAfterLogin();
                    break;
                case "logout":
                    try {
                        server.logout(authToken);
                        return;
                    }
                    catch (ResponseException e) {
                        printError(e.getMessage());
                        break;
                    }
                case "create":
                    create(in);
                    break;
                case "list":
                    list();
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

    private void helpBeforeLogin() {
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

    private void helpAfterLogin() {
        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + "logout"
                + EscapeSequences.SET_TEXT_COLOR_MAGENTA + " - log out of your account");
        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + "create <NAME>"
                + EscapeSequences.SET_TEXT_COLOR_MAGENTA + " - create a game");
        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + "list"
                + EscapeSequences.SET_TEXT_COLOR_MAGENTA + " - list all games");
        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + "join <ID> [WHITE|BLACK]"
                + EscapeSequences.SET_TEXT_COLOR_MAGENTA + " - join a game");
        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + "observe <ID>"
                + EscapeSequences.SET_TEXT_COLOR_MAGENTA + " - observe a game");
        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + "help"
                + EscapeSequences.SET_TEXT_COLOR_MAGENTA + " - print this help message");
        System.out.print(EscapeSequences.RESET_TEXT_COLOR);
    }

    private boolean login(Scanner in) {
        if (in.hasNext()) {
            String username = in.next();
            if (in.hasNext()) {
                String password = in.next();
                try {
                    authToken = server.login(username, password).authToken();
                    this.username = username;
                    return true;
                }
                catch (ResponseException e) {
                    printError(e.getMessage());
                    return false;
                }
            }
            else {
                printError("Missing password");
                return false;
            }
        }
        else {
            return loginGetInfo(in);
        }
    }

    private boolean register(Scanner in) {
        if (in.hasNext()) {
            String username = in.next();
            if (in.hasNext()) {
                String password = in.next();
                if (in.hasNext()) {
                    String email = in.next();
                    try {
                        authToken = server.register(username, password, email).authToken();
                        this.username = username;
                        return true;
                    }
                    catch (ResponseException e) {
                        printError(e.getMessage());
                        return false;
                    }
                }
                else {
                    printError("Missing email address.");
                    return false;
                }
            }
            else {
                printError("Missing password and email address.");
                return false;
            }
        }
        else {
            return registerGetInfo(in);
        }
    }

    private boolean registerGetInfo(Scanner in) {
        System.out.print("Username: ");
        String username = in.next();
        System.out.print("Password: ");
        String password = in.next();
        System.out.print("Email: ");
        String email = in.next();
        try {
            authToken = server.register(username, password, email).authToken();
            this.username = username;
            return true;
        }
        catch (ResponseException e) {
            printError(e.getMessage());
            return false;
        }
    }

    private boolean loginGetInfo(Scanner in) {
        System.out.print("Username: ");
        String username = in.next();
        System.out.print("Password: ");
        String password = in.next();
        try {
            authToken = server.login(username, password).authToken();
            this.username = username;
            return true;
        }
        catch (ResponseException e) {
            printError(e.getMessage());
            return false;
        }
    }

    private void create(Scanner in) {
        if (!in.hasNext()) {
            System.out.print("Game name: ");
        }
        try {
            in.skip(" *");
            server.create(in.nextLine(), authToken);
        }
        catch (ResponseException e) {
            printError(e.getMessage());
        }
    }

    private void list() {
        try {
            GameData[] games = server.list(authToken).games();
            System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + "Current games:" + EscapeSequences.RESET_TEXT_COLOR);
            for (int i = 1; i <= games.length; i++) {
                GameData game = games[i-1];
                System.out.println(i + ": " + game.gameName());
                System.out.println("  White: " + EscapeSequences.SET_TEXT_COLOR_MAGENTA +
                        (game.whiteUsername() == null ? "Free" : game.whiteUsername()) + EscapeSequences.RESET_TEXT_COLOR);
                System.out.println("  Black: " + EscapeSequences.SET_TEXT_COLOR_MAGENTA +
                        (game.blackUsername() == null ? "Free" : game.blackUsername()) + EscapeSequences.RESET_TEXT_COLOR);
            }
        } catch (ResponseException e) {
            printError(e.getMessage());
        }
    }

    private void printError(String error) {
        System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "Error: " + error + EscapeSequences.RESET_TEXT_COLOR);
    }
}
