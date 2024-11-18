import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("♕ Welcome to 240 Chess. Type Help to get started. ♕");

        beforeLogin();
    }

    public static void beforeLogin() {
        Scanner in = new Scanner(System.in);
        while (true) {
            System.out.println("[LOGGED_OUT] >>> ");
            String userInput = in.next("[ \n]");
            switch (userInput) {
                case "help":
                    helpBeforeLogin();
                    break;
                case "login":
                    login(in);
                    break;
                case "register":
                    register(in);
                    break;
                case "quit":
                    return;
                default:
                    System.out.println("Unrecognized command. For a list of available commands, type \"help\"");
            }
        }
    }

    public static void helpBeforeLogin() {
        System.out.println();
    }

    public static void helpAfterLogin() {

    }

    public static void login(Scanner in) {

    }

    public static void register(Scanner in) {

    }
}