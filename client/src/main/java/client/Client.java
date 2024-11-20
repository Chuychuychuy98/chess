package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import exceptions.ResponseException;
import model.GameData;
import serverfacade.ServerFacade;
import ui.EscapeSequences;

import java.util.Scanner;

public class Client {
    String authToken = null;
    String username = null;
    ServerFacade server;
    GameData[] games = null;

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
            String userInput = in.nextLine();
            String[] args = userInput.split(" +");
            switch (args[0].toLowerCase()) {
                case "help":
                    helpBeforeLogin();
                    break;
                case "login":
                    if (args.length > 3) {
                        printError("Too many arguments given. " + EscapeSequences.SET_TEXT_COLOR_BLUE + "login" +
                                EscapeSequences.SET_TEXT_COLOR_RED + " takes a username and a password.");
                    }
                    else if (args.length == 2) {
                        printError("Missing password.");
                    }
                    else if (args.length == 3) {
                        if (loginArgsProvided(args[1], args[2])) {
                            afterLogin(in);
                        }
                    }
                    else {
                        if (loginGetArgs(in)) {
                            afterLogin(in);
                        }
                    }
                    break;
                case "register":
                    if (args.length > 4) {
                        printError("Too many arguments given. " + EscapeSequences.SET_TEXT_COLOR_BLUE + "register" +
                                EscapeSequences.SET_TEXT_COLOR_RED + " takes a username, password, and email.");
                    }
                    else if (args.length == 3) {
                        printError("Missing email.");
                    }
                    else if (args.length == 2) {
                        printError("Missing password and email.");
                    }
                    else if (args.length == 1) {
                        if (registerGetArgs(in)) {
                            afterLogin(in);
                        }
                    }
                    else {
                        if (registerArgsProvided(args[1], args[2], args[2])) {
                            afterLogin(in);
                        }
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
            String userInput = in.nextLine();
            String[] args = userInput.split(" +");
            switch (args[0].toLowerCase()) {
                case "help":
                    helpAfterLogin();
                    break;
                case "logout":
                    if (args.length > 1) {
                        printError(EscapeSequences.SET_TEXT_COLOR_BLUE + "logout" +
                                EscapeSequences.SET_TEXT_COLOR_RED + "does not take any arguments.");
                        break;
                    }
                    try {
                        server.logout(authToken);
                        return;
                    }
                    catch (ResponseException e) {
                        printError(e.getMessage());
                        break;
                    }
                case "create":
                    if (args.length == 1) {
                        createGetName(in);
                    }
                    else {
                        try {
                            server.create(userInput.substring(userInput.indexOf(args[1])), authToken);
                        }
                        catch (ResponseException e) {
                            printError(e.getMessage());
                        }
                    }
                    break;
                case "list":
                    if (args.length > 1) {
                        printError(EscapeSequences.SET_TEXT_COLOR_BLUE + "list" +
                                EscapeSequences.SET_TEXT_COLOR_RED + " does not take any arguments.");
                        break;
                    }
                    list();
                    break;
                case "join":
                    if (args.length > 3) {
                        printError("Too many arguments given. " + EscapeSequences.SET_TEXT_COLOR_BLUE + "join" +
                                EscapeSequences.SET_TEXT_COLOR_RED + " takes an ID and a team color");
                    }
                    else if (args.length == 2) {
                        printError("Missing team color.");
                    }
                    else if (args.length == 1) {
                        joinGetArgs(in);
                    }
                    else {
                        joinArgsProvided(args[1], args[2]);
                    }
                    break;
                case "observe":
                    if (args.length > 2) {
                        printError("Too many arguments given. " + EscapeSequences.SET_TEXT_COLOR_BLUE + "observe" +
                                EscapeSequences.SET_TEXT_COLOR_RED + " takes only an ID");
                    }
                    else if (args.length == 1){
                        observeGetArgs(in);
                    }
                    else {
                        observeArgsProvided(args[1]);
                    }
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

    private String getInput(Scanner in, String prompt) {
        String input;
        while (true) {
            System.out.print(prompt + ": ");
            input = in.nextLine();
            if (input.contains(" ")) {
                printError(prompt + " must not contain spaces.");
            }
            else {
                return input;
            }
        }
    }

    private boolean loginGetArgs(Scanner in) {
        String username = getInput(in, "Username");
        String password = getInput(in, "Password");
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

    private boolean loginArgsProvided(String username, String password) {
        if (username.contains(" ")) {
            printError("Username must be one word.");
            return false;
        }
        if (password.contains(" ")) {
            printError("Password must be one word.");
            return false;
        }
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

    private boolean registerArgsProvided(String username, String password, String email) {
            if (username.contains(" ")) {
                printError("Username must be one word.");
                return false;
            }
            if (password.contains(" ")) {
                printError("Password must be one word.");
                return false;
            }
            if (email.contains(" ")) {
                printError("Email may not contain spaces.");
                return false;
            }
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

    private boolean registerGetArgs(Scanner in) {
        String username = getInput(in, "Username");
        String password = getInput(in, "Password");
        String email = getInput(in, "Email");
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

    private void createGetName(Scanner in) {
        System.out.print("Game name: ");
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
            games = server.list(authToken).games();
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

    private int joinObserveCommon(String idString) {
        if (games == null) {
            System.out.println("You must first list the games with "+ EscapeSequences.SET_TEXT_COLOR_BLUE
                    + "list" + EscapeSequences.RESET_TEXT_COLOR);
            return -1;
        }
        int id;
        try {
            id = Integer.parseInt(idString);
        }
        catch (NumberFormatException e) {
            printError("Please proved a numerical ID.");
            return -1;
        }
        return id;
    }

    private void joinArgsProvided(String idString, String teamString) {
        int id = joinObserveCommon(idString);
        if (id < 0) {
            return;
        }
        if (id == 0 || id > games.length) {
            printError("Game ID must correspond to an existing game.");
            return;
        }
        if (!teamString.equals("white") && !teamString.equals("black")) {
            printError("Team color must be WHITE or BLACK.");
            return;
        }

        try {
            server.join(id, teamString.equals("white") ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK, authToken);
            print(games[id-1].game().getBoard());
        }
        catch (ResponseException e) {
            printError(e.getMessage());
        }
    }

    private void joinGetArgs(Scanner in) {
        if (games == null) {
            System.out.println("You must first list the games with "+ EscapeSequences.SET_TEXT_COLOR_BLUE
                    + "list" + EscapeSequences.RESET_TEXT_COLOR);
            return;
        }

        System.out.print("Game ID: ");
        int id = getId(in);

        try {
            ChessGame.TeamColor team;
            String input;
            while (true) {
                System.out.print("Team Color: ");
                input = in.next().toLowerCase();
                if (input.equals("white")) {
                    team = ChessGame.TeamColor.WHITE;
                    break;
                }
                else if (input.equals("black")) {
                    team = ChessGame.TeamColor.BLACK;
                    break;
                }
                else {
                    printError("Please input either BLACK or WHITE.");
                    if (in.hasNext()) {
                        in.nextLine();
                    }
                }
            }
            server.join(games[id-1].gameID(), team, authToken);
            print(games[id-1].game().getBoard());
        }
        catch (ResponseException e) {
            printError(e.getMessage());
        }
    }

    private int getId(Scanner in) {
        int id = 0;
        System.out.print("Game ID: ");
        while (id <= 0 || id > games.length) {
            String line = in.nextLine();
            try {
                id = Integer.parseInt(line);
            } catch (NumberFormatException e) {
                printError("Please input a single integer game ID.");
            }
            if (id <= 0 || id > games.length) {
                printError("Please input a value corresponding to an existing game.");
            }
        }
        return id;
    }

    private void observeArgsProvided(String idString) {
        int id = joinObserveCommon(idString);
        if (id < 0) {
            return;
        }
        else if (id == 0 || id > games.length) {
            printError("Game ID must correspond to an existing game.");
            return;
        }
        print(games[id-1].game().getBoard());
    }

    private void observeGetArgs(Scanner in) {
        if (games == null) {
            System.out.println("You must first list the games with "+ EscapeSequences.SET_TEXT_COLOR_BLUE
                    + "list" + EscapeSequences.RESET_TEXT_COLOR);
            return;
        }
        int id = getId(in);
        print(games[id-1].game().getBoard());
    }

    private void printError(String error) {
        System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "Error: " + error + EscapeSequences.RESET_TEXT_COLOR);
    }

    private void print(ChessBoard board) {
        printWhiteTop(board.getPieces());
        printBlackTop(board.getPieces());
    }

    private void printBlackTop(ChessPiece[][] pieces) {
        System.out.print("   ");
        for (int i = 0; i < 8; i++) {
            System.out.print(" " + Character.toString('ａ' + i) + " ");
        }
        System.out.println("   " + EscapeSequences.RESET_BG_COLOR);
        for (int i = 7; i >= 0; i--) {
            System.out.print(" " + (i+1) + " ");
            for (int j = 0; j < 8; j++) {
                printCell(pieces, i, j);
            }
            System.out.print(EscapeSequences.RESET_BG_COLOR);
            System.out.print(" " + (i+1) + " ");
            System.out.println(EscapeSequences.RESET_BG_COLOR);
        }
        System.out.print("   ");
        for (int i = 0; i < 8; i++) {
            System.out.print(" " + Character.toString('ａ' + i) + " ");
        }
        System.out.println("   " + EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_BG_COLOR);
    }

    private void printWhiteTop(ChessPiece[][] pieces) {
        System.out.print("   ");
        for (int i = 0; i < 8; i++) {
            System.out.print(" " + Character.toString('ｈ' - i) + " ");
        }
        System.out.println("   " + EscapeSequences.RESET_BG_COLOR);
        for (int i = 0; i < 8; i++) {
            System.out.print(" " + (i+1) + " ");
            for (int j = 7; j >= 0; j--) {
                printCell(pieces, i, j);
            }
            System.out.print(EscapeSequences.RESET_BG_COLOR);
            System.out.print(" " + (i+1) + " ");
            System.out.println(EscapeSequences.RESET_BG_COLOR);
        }
        System.out.print("   ");
        for (int i = 0; i < 8; i++) {
            System.out.print(" " + Character.toString('ｈ' - i) + " ");
        }
        System.out.println("   " + EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_BG_COLOR);
    }

    private void printCell(ChessPiece[][] pieces, int i, int j) {
        if (j % 2 != i % 2) {
            System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
        }
        else {
            System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY);
        }
        ChessPiece piece = pieces[i][j];
        if (piece == null) {
            System.out.print(EscapeSequences.EMPTY);
            return;
        }

        String pieceString = getPieceString(piece);
        System.out.print(pieceString + EscapeSequences.RESET_TEXT_COLOR);
    }

    private String getPieceString(ChessPiece piece) {
        String pieceString;
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            pieceString = EscapeSequences.SET_TEXT_COLOR_WHITE +
                switch (piece.getPieceType()) {
                    case KING -> EscapeSequences.WHITE_KING;
                    case QUEEN -> EscapeSequences.WHITE_QUEEN;
                    case BISHOP -> EscapeSequences.WHITE_BISHOP;
                    case KNIGHT -> EscapeSequences.WHITE_KNIGHT;
                    case ROOK -> EscapeSequences.WHITE_ROOK;
                    case PAWN ->  EscapeSequences.WHITE_PAWN;
                };
        }
        else {
            pieceString = EscapeSequences.SET_TEXT_COLOR_BLACK +
                    switch (piece.getPieceType()) {
                        case KING -> EscapeSequences.BLACK_KING;
                        case QUEEN -> EscapeSequences.BLACK_QUEEN;
                        case BISHOP -> EscapeSequences.BLACK_BISHOP;
                        case KNIGHT -> EscapeSequences.BLACK_KNIGHT;
                        case ROOK -> EscapeSequences.BLACK_ROOK;
                        case PAWN ->  EscapeSequences.BLACK_PAWN;
                    };
        }
        return pieceString;
    }
}
