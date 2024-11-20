package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import ui.EscapeSequences;

public class Utils {

    public static void printError(String error) {
        System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "Error: " + error + EscapeSequences.RESET_TEXT_COLOR);
    }

    public static void printBoard(ChessBoard board) {
        printWhiteTop(board.getPieces());
        printBlackTop(board.getPieces());
    }

    private static void printBlackTop(ChessPiece[][] pieces) {
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

    private static void printWhiteTop(ChessPiece[][] pieces) {
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

    private static void printCell(ChessPiece[][] pieces, int i, int j) {
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
}
