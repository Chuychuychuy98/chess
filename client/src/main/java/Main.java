import ui.EscapeSequences;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("♕ Welcome to 240 Chess. Type Help to get started. ♕");
        beforeLogin();
    }

    public static void beforeLogin() {
        Scanner in = new Scanner(System.in);
        while (true) {
            System.out.print("[LOGGED_OUT] >>> ");
            String userInput = in.nextLine();
            String command = userInput;
            int commandEnd = userInput.indexOf(" ");
            if (commandEnd != -1) {
                command = userInput.substring(0, commandEnd);
            }
            switch (command) {
                case "help":
                    helpBeforeLogin();
                    break;
                case "login":
                    login(userInput);
                    break;
                case "register":
                    register(userInput);
                    break;
                case "quit":
                    return;
                default:
                    System.out.println("Unrecognized command. For a list of available commands, type \"help\"");
            }
        }
    }

    public static void helpBeforeLogin() {
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

    public static void helpAfterLogin() {

    }

    public static void login(String input) {

    }

    public static void register(String input) {

    }
}