package client;

import chess.*;
import model.GameData;
import ui.EscapeSequences;

import java.util.Collection;
import java.util.Collections;
import java.util.Scanner;

public class Utils {

    static void printError(String error) {
        System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "Error: " + error + EscapeSequences.RESET_TEXT_COLOR);
    }

    static void displayNotification(String notification, String username) {
        System.out.println(notification);
        System.out.print("[" + username + "] >>> ");
    }

    static void loadGame(GameData game) {
        printBoard(game.game().getBoard());
    }

    static void printBoard(ChessBoard board) {
        printWhiteTop(board.getPieces());
        printBlackTop(board.getPieces());
    }

    static void printBlackTop(ChessPiece[][] pieces) {
        printBlackTop(pieces, Collections.emptyList());
    }

    static void printBlackTop(ChessPiece[][] pieces, Collection<ChessPosition> highlight) {
        System.out.print("   ");
        for (int i = 0; i < 8; i++) {
            System.out.print(" " + Character.toString('ａ' + i) + " ");
        }
        System.out.println("   " + EscapeSequences.RESET_BG_COLOR);
        for (int i = 7; i >= 0; i--) {
            System.out.print(" " + (i+1) + " ");
            for (int j = 0; j < 8; j++) {
                printCell(pieces, i, j, highlight.contains(ChessPosition.getPosition(i+1, j+1)));
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

    static void printWhiteTop(ChessPiece[][] pieces) {
        printWhiteTop(pieces, Collections.emptyList());
    }

    static void printWhiteTop(ChessPiece[][] pieces, Collection<ChessPosition> highlight) {
        System.out.print("   ");
        for (int i = 0; i < 8; i++) {
            System.out.print(" " + Character.toString('ｈ' - i) + " ");
        }
        System.out.println("   " + EscapeSequences.RESET_BG_COLOR);
        for (int i = 0; i < 8; i++) {
            System.out.print(" " + (i+1) + " ");
            for (int j = 7; j >= 0; j--) {
                printCell(pieces, i, j, highlight.contains(ChessPosition.getPosition(i+1, j+1)));
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

    private static void printCell(ChessPiece[][] pieces, int i, int j, boolean highlight) {
        String blackColor;
        String whiteColor;
        if (highlight) {
            blackColor = EscapeSequences.SET_BG_COLOR_DARK_GREEN;
            whiteColor = EscapeSequences.SET_BG_COLOR_GREEN;
        }
        else {
            blackColor = EscapeSequences.SET_BG_COLOR_DARK_GREY;
            whiteColor = EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
        }
        if (j % 2 != i % 2) {
            System.out.print(whiteColor);
        }
        else {
            System.out.print(blackColor);
        }
        ChessPiece piece = pieces[i][j];
        if (piece == null) {
            System.out.print(EscapeSequences.EMPTY);
            return;
        }

        String pieceString = getPieceString(piece);
        System.out.print(pieceString + EscapeSequences.RESET_TEXT_COLOR);
    }

    private static String getPieceString(ChessPiece piece) {
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

    static int getId(Scanner in, GameData[] games) {
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

    static String getInput(Scanner in, String prompt) {
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

    static ChessPosition getSquare(Scanner in, String prompt) {
        while (true) {
            System.out.print(prompt + ": ");
            String userInput = in.nextLine().strip().toLowerCase();
            ChessPosition pos = parsePosition(userInput);
            if (pos != null) {
                return pos;
            }
        }
    }

    static ChessPosition parsePosition(String posString) {
        if (posString.length() != 2) {
            printError("Chess square must be exactly one letter followed by exactly one number.");
            return null;
        }
        else if (posString.charAt(0) < 'a' || posString.charAt(0) > 'h') {
            printError("First character must be a letter between a and h.");
            return null;
        }
        else if (posString.charAt(1) < '1' || posString.charAt(1) > '8') {
            printError("Second character must be a number between 1 and 8.");
            return null;
        }
        else {
            return ChessPosition.getPosition(Integer.parseInt(String.valueOf(posString.charAt(1))),
            (posString.charAt(0) + 1) - 'a');
        }
    }

    static ChessPiece.PieceType getPromotion(Scanner in, ChessPiece.PieceType type, int row, ChessGame.TeamColor color) {
        if (type == ChessPiece.PieceType.PAWN &&
                ((color == ChessGame.TeamColor.WHITE && row == 8) ||
                (color == ChessGame.TeamColor.BLACK && row == 1))) {

            while (true) {
                System.out.print("Piece to promote to: ");
                ChessPiece.PieceType newType = switch (in.nextLine().toLowerCase().strip()) {
                    case "pawn", "p" -> ChessPiece.PieceType.PAWN;
                    case "knight", "n" -> ChessPiece.PieceType.KNIGHT;
                    case "bishop", "b" -> ChessPiece.PieceType.BISHOP;
                    case "rook", "r" -> ChessPiece.PieceType.ROOK;
                    case "queen", "q" -> ChessPiece.PieceType.QUEEN;
                    default -> {
                        printError("Invalid promotion type.");
                        yield null;
                    }
                };
                if (newType != null) {
                    return newType;
                }
            }
        }
        else {
            return null;
        }
    }

    static void helpBeforeLogin() {
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

    static void helpAfterLogin() {
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

    static void helpInGame() {
        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + "redraw"
                + EscapeSequences.SET_TEXT_COLOR_MAGENTA + " - redraw the board");
        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + "leave"
                + EscapeSequences.SET_TEXT_COLOR_MAGENTA + " - leave the match");
        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + "move <from> <to>"
                + EscapeSequences.SET_TEXT_COLOR_MAGENTA + " - make a move (<from> and <to> are squares notated as, e.g., \"a3\")");
        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + "resign"
                + EscapeSequences.SET_TEXT_COLOR_MAGENTA + " - concede the match");
        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + "highlight " + EscapeSequences.RESET_TEXT_COLOR + "or"
                + EscapeSequences.SET_TEXT_COLOR_BLUE + " moves"
                + EscapeSequences.SET_TEXT_COLOR_MAGENTA + " - highlight legal moves in green");
        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + "help"
                + EscapeSequences.SET_TEXT_COLOR_MAGENTA + " - print this help message");
        System.out.print(EscapeSequences.RESET_TEXT_COLOR);
    }

    static void highlightMoves(ChessGame.TeamColor color, GameData data, ChessPosition position) {
        if (color == null) {
            printError("You are not currently playing the game.");
            return;
        }
        if (data == null) {
            printError("You must join a game before you can highlight legal moves.");
            return;
        }
        if (position == null) {
            printError("Invalid position.");
            return;
        }
        if (data.game().getBoard().getPiece(position) == null) {
            printError("There is no piece at position " + position.chessNotation());
            return;
        }
        if (data.game().getBoard().getPiece(position).getTeamColor() != color) {
            printError("That is not your piece.");
            return;
        }

        if (color == ChessGame.TeamColor.BLACK) {
            printWhiteTop(data.game().getBoard().getPieces(), data.game().validEndPositions(position));
        }
        else {
            printBlackTop(data.game().getBoard().getPieces(), data.game().validEndPositions(position));
        }
    }
}
