package chess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private final int BOARD_ROWS = 8;
    private final int BOARD_COLS = 8;
    private final ChessPiece[][] board = new ChessPiece[BOARD_ROWS][BOARD_COLS];

    public ChessBoard() {
        
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;

        for (int i = 0; i < BOARD_ROWS; i++) {
            for (int j = 0; j < BOARD_COLS; j++) {
                if (board[i][j] == null) {
                    if (that.board[i][j] != null) {
                        return false;
                    }
                }
                else if (that.board[i][j] == null) {
                    return false;
                }
                else if (!board[i][j].equals(that.board[i][j])) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(board);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            sb.append("|");
            for (int j = 0; j < 8; j++) {
                sb.append(board[i][j] != null ? board[i][j].toString() : " ");
                sb.append("|");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        if (board[position.getRow()-1][position.getColumn()-1] == null) {
            board[position.getRow()-1][position.getColumn()-1] = piece;
        }
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Places a row of non-pawn pieces on a given row for a given color. Assumes normal chess setup.
     * @param row The row to place the pieces on.
     * @param color The color of the pieces.
     */
    private void placeSpecialPieces(int row, ChessGame.TeamColor color) {
        board[row][0] = new ChessPiece(color, ChessPiece.PieceType.ROOK);
        board[row][1] = new ChessPiece(color, ChessPiece.PieceType.KNIGHT);
        board[row][2] = new ChessPiece(color, ChessPiece.PieceType.BISHOP);
        board[row][3] = new ChessPiece(color, ChessPiece.PieceType.QUEEN);
        board[row][4] = new ChessPiece(color, ChessPiece.PieceType.KING);
        board[row][5] = new ChessPiece(color, ChessPiece.PieceType.BISHOP);
        board[row][6] = new ChessPiece(color, ChessPiece.PieceType.KNIGHT);
        board[row][7] = new ChessPiece(color, ChessPiece.PieceType.ROOK);
    }

    /**
     * Places pawns of a specified color on the specified row.
     * @param row The row to place the pawns on.
     * @param color The color of the pawns.
     */
    private void placePawns(int row, ChessGame.TeamColor color) {
        for (int i = 0; i < BOARD_COLS; i++) {
            board[row][i] = new ChessPiece(color, ChessPiece.PieceType.PAWN);
        }
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        placeSpecialPieces(0, ChessGame.TeamColor.WHITE);
        placeSpecialPieces(7, ChessGame.TeamColor.BLACK);

        placePawns(1, ChessGame.TeamColor.WHITE);
        placePawns(6, ChessGame.TeamColor.BLACK);
    }
}
