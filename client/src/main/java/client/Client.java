package client;

import chess.*;
import exceptions.ResponseException;
import model.GameData;
import serverfacade.ServerFacade;
import ui.EscapeSequences;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.Scanner;

import static client.Utils.*;

public class Client {
    private String authToken = null;
    private String username = null;
    private ServerFacade server;
    private GameData[] games = null;
    private ChessGame.TeamColor color = null;
    private GameData curGame = null;

    public void setServer(ServerFacade server) {
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
                        if (joinGetArgs(in)) {
                            inGame(in);
                        }
                    }
                    else {
                        if (joinArgsProvided(args[1], args[2])) {
                            inGame(in);
                        }
                    }
                    break;
                case "observe":
                    if (args.length > 2) {
                        printError("Too many arguments given. " + EscapeSequences.SET_TEXT_COLOR_BLUE + "observe" +
                                EscapeSequences.SET_TEXT_COLOR_RED + " takes only an ID");
                    }
                    else if (args.length == 1){
                        if (observeGetArgs(in)) {
                            inGame(in);
                        }
                    }
                    else {
                        if (observeArgsProvided(args[1])) {
                            inGame(in);
                        }
                    }
                    break;
                default:
                    System.out.println("Unrecognized command. For a list of available commands, type \"help\"");
            }
        }
    }

    private void inGame(Scanner in) {
        boolean print = true;
        while (true) {
            if (print) {
                if (color == ChessGame.TeamColor.WHITE) {
                    printBlackTop(curGame.game().getBoard().getPieces());
                } else {
                    printWhiteTop(curGame.game().getBoard().getPieces());
                }
            }
            if (color == curGame.game().getTeamTurn()) {
                System.out.println("It's your turn!");
            }
            else {
                System.out.println("It's " + curGame.game().getTeamTurn() + "'s turn.");
            }
            System.out.printf("[%s] >>> ", username);
            String userInput = in.nextLine();
            String[] args = userInput.split(" +");
            switch (args[0].toLowerCase()) {
                case "help":
                    Utils.helpInGame();
                    print = true;
                    break;
                case "redraw":
                    System.out.print(EscapeSequences.ERASE_SCREEN);
                    if (color == ChessGame.TeamColor.BLACK) {
                        Utils.printWhiteTop(curGame.game().getBoard().getPieces());
                    }
                    else {
                        Utils.printBlackTop(curGame.game().getBoard().getPieces());
                    }
                    print = false;
                    break;
                case "leave":
                    if (server.leave(authToken, curGame.gameID())) {
                        return;
                    }
                    break;
                case "move":
                    if (args.length > 3) {
                        printError("Too many arguments given. " + EscapeSequences.SET_TEXT_COLOR_BLUE + "move" +
                                EscapeSequences.SET_TEXT_COLOR_RED + " takes a square to move from and a square to move to.");
                    }
                    else if (args.length == 2) {
                        printError("Missing destination square.");
                    }
                    else if (args.length == 1) {
                        moveGetArgs(in);
                    }
                    else {
                        moveArgsProvided(args[1], args[2], in);
                    }
                    print = true;
                    break;
                case "resign":
                    System.out.println("Are you sure you want to resign? (Y/N): ");
                    if ((userInput = in.nextLine().strip().toLowerCase()).equals("y") || userInput.equals("yes")) {
                        resign();
                    }
                    print = true;
                    break;
                case "highlight":
                case "moves":
                    if (args.length > 2) {
                        printError("Too many arguments given. " + EscapeSequences.SET_TEXT_COLOR_BLUE + args[0] +
                                EscapeSequences.SET_TEXT_COLOR_RED + " takes a position to highlight moves for.");
                    }
                    else if (args.length == 2) {
                        highlightArgsProvided(args[1]);
                    }
                    else {
                        highlightGetArgs(in);
                    }
                    print = false;
                    break;
                default:
                    System.out.println("Unrecognized command. For a list of available commands, type \"help\"");
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

    private boolean joinArgsProvided(String idString, String teamString) {
        int id = joinObserveCommon(idString);
        if (id < 0) {
            return false;
        }
        if (id == 0 || id > games.length) {
            printError("Game ID must correspond to an existing game.");
            return false;
        }
        if (!teamString.equals("white") && !teamString.equals("black")) {
            printError("Team color must be WHITE or BLACK.");
            return false;
        }

        try {
            curGame = games[id-1];
            server.join(curGame.gameID(), teamString.equals("white") ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK, authToken);
            color = ChessGame.TeamColor.valueOf(teamString.toUpperCase());
            return true;
        }
        catch (ResponseException e) {
            printError(e.getMessage());
            return false;
        }
    }

    private boolean joinGetArgs(Scanner in) {
        if (games == null) {
            System.out.println("You must first list the games with "+ EscapeSequences.SET_TEXT_COLOR_BLUE
                    + "list" + EscapeSequences.RESET_TEXT_COLOR);
            return false;
        }

        int id = getId(in, games);

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
            curGame = games[id-1];
            server.join(curGame.gameID(), team, authToken);
            color = team;
            return true;
        }
        catch (ResponseException e) {
            printError(e.getMessage());
            return false;
        }
    }

    private boolean observeArgsProvided(String idString) {
        int id = joinObserveCommon(idString);
        if (id < 0) {
            return false;
        }
        else if (id == 0 || id > games.length) {
            printError("Game ID must correspond to an existing game.");
            return false;
        }
        curGame = games[id-1];
        server.observe(authToken, curGame.gameID());
        return true;
    }

    private boolean observeGetArgs(Scanner in) {
        if (games == null) {
            System.out.println("You must first list the games with "+ EscapeSequences.SET_TEXT_COLOR_BLUE
                    + "list" + EscapeSequences.RESET_TEXT_COLOR);
            return false;
        }
        int id = getId(in, games);
        curGame = games[id-1];
        server.observe(authToken, curGame.gameID());
        return true;
    }

    private void moveGetArgs(Scanner in) {
        ChessPosition from = Utils.getSquare(in, "From");
        ChessPosition to = Utils.getSquare(in, "To");

        ChessPiece.PieceType promotionPiece = Utils.getPromotion(in,
                curGame.game().getBoard().getPiece(from).getPieceType(), to.getRow(), color);
        server.move(authToken, curGame.gameID(), new ChessMove(from, to, promotionPiece));
    }

    private void moveArgsProvided(String fromString, String toString, Scanner in) {
        ChessPosition from = Utils.parsePosition(fromString);
        if (from == null) {
            printError("<From> string formatted incorrectly. A chess square is notated with a letter for the column" +
                    " and a number for the row, as in \"a1\".");
            return;
        }
        ChessPosition to = Utils.parsePosition(toString);
        if (to == null) {
            printError("<To> string formatted incorrectly. A chess square is notated with a letter for the column" +
                    " and a number for the row, as in \"a1\".");
            return;
        }
        ChessPiece.PieceType promotionPiece = Utils.getPromotion(in,
                curGame.game().getBoard().getPiece(from).getPieceType(), to.getRow(), color);

        server.move(authToken, curGame.gameID(), new ChessMove(from, to, promotionPiece));
    }

    private void resign() {
        try {
            curGame.game().setGameOver();
            server.resign(authToken, curGame.gameID());
        }
        catch (GameOverException e) {
            printError("The game is already over.");
        }
    }

    private void highlightGetArgs(Scanner in) {
        ChessPosition pos = Utils.getSquare(in, "Piece");
        Utils.highlightMoves(color, curGame, pos);
    }

    private void highlightArgsProvided(String position) {
        Utils.highlightMoves(color, curGame, parsePosition(position));
    }

    public void notify(ServerMessage msg) {
        switch (msg.getServerMessageType()) {
            case NOTIFICATION -> displayNotification(((NotificationMessage) msg).getMessage());
            case ERROR -> printError(((ErrorMessage) msg).getMessage());
            case LOAD_GAME -> {
                curGame = ((LoadGameMessage) msg).getGame();
                printBoard(curGame.game().getBoard());
            }
        }
    }
}
