package chess;

import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private final ChessPiece[][] pieces;

    public ChessBoard() {
        pieces = new ChessPiece[8][8];
    }

    public static ChessBoard defaultBoard() {
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        return board;
    }

    public ChessBoard copyOf() {
        ChessBoard chessBoard = new ChessBoard();
        for (int i = 0; i < 8; i++) {
            System.arraycopy(pieces[i], 0, chessBoard.pieces[i], 0, 8);
        }
        return chessBoard;
    }

    public void copyBoard(ChessBoard copy) {
        for (int i = 0; i < 8; i++) {
            System.arraycopy(copy.pieces[i], 0, pieces[i], 0, 8);
        }
    }


    public void doMove(ChessMove move) {
        ChessPiece pieceToMove = getPiece(move.getStartPosition());
        if (move.getPromotionPiece() != null) {
            pieceToMove.setPieceType(move.getPromotionPiece());
        }
        pieceToMove.setMoved();
        removePiece(move.getStartPosition());
        addPiece(move.getEndPosition(), pieceToMove);

    }

    public void simMove(ChessMove move) {
        ChessPiece pieceToMove = getPiece(move.getStartPosition()).movedCopy();
        removePiece(move.getStartPosition());
        addPiece(move.getEndPosition(), pieceToMove);
    }

    public void removePiece(ChessPosition position) {
        pieces[position.getRow()-1][position.getColumn()-1] = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(pieces, that.pieces);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(pieces);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= 8; i++) {
            sb.append("|");
            for (int j = 1; j <= 8; j++) {
                ChessPosition cur = ChessPosition.getPosition(i,j);
                sb.append(getPiece(cur) != null ? getPiece(cur).toString() : " ");
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
        //pieces.put(position, piece);
        pieces[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return pieces[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Places a row of non-pawn pieces on a given row for a given color. Assumes normal chess setup.
     * @param row The row to place the pieces on.
     * @param color The color of the pieces.
     */
    private void placeSpecialPieces(int row, ChessGame.TeamColor color) {
        pieces[row][0] = new ChessPiece(color, ChessPiece.PieceType.ROOK);
        pieces[row][1] = new ChessPiece(color, ChessPiece.PieceType.KNIGHT);
        pieces[row][2] = new ChessPiece(color, ChessPiece.PieceType.BISHOP);
        pieces[row][3] = new ChessPiece(color, ChessPiece.PieceType.QUEEN);
        pieces[row][4] = new ChessPiece(color, ChessPiece.PieceType.KING);
        pieces[row][5] = new ChessPiece(color, ChessPiece.PieceType.BISHOP);
        pieces[row][6] = new ChessPiece(color, ChessPiece.PieceType.KNIGHT);
        pieces[row][7] = new ChessPiece(color, ChessPiece.PieceType.ROOK);

    }

    /**
     * Places pawns of a specified color on the specified row.
     * @param row The row to place the pawns on.
     * @param color The color of the pawns.
     */
    private void placePawns(int row, ChessGame.TeamColor color) {
        for (int i = 0; i < 8; i++) {
            pieces[row][i] = new ChessPiece(color, ChessPiece.PieceType.PAWN);
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

    /**
     * Returns whether a given position is occupied by an enemy piece.
     * @param pos The position to check.
     * @param color The friendly piece's color.
     * @return Whether the given position is occupied by an enemy piece.
     */
    public boolean positionBlockedByEnemy(ChessPosition pos, ChessGame.TeamColor color) {
        ChessPiece piece = getPiece(pos);
        if (piece == null) {
            return false;
        }
        return piece.getTeamColor() != color;
    }

    /**
     * Returns whether a given position is empty or occupied by an enemy piece.
     * @param pos The position to check
     * @param color The friendly piece's color
     * @return Whether the given position is empty or occupied by an enemy piece
     */
    public boolean canMoveOrCapture(ChessPosition pos, ChessGame.TeamColor color) {
        return pos.inBounds() && (getPiece(pos) == null || positionBlockedByEnemy(pos, color));
    }

    public boolean isInCheck(ChessGame.TeamColor team) {
        ChessPosition kingPos = null;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPiece piece = pieces[i][j];
                if (piece != null && piece.getTeamColor() == team && piece.getPieceType() == ChessPiece.PieceType.KING) {
                    kingPos = ChessPosition.getPosition(i+1, j+1);
                    break;
                }
            }
        }

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (isSpaceAttackingKing(i, j, team, kingPos)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isSpaceAttackingKing(int row, int col, ChessGame.TeamColor team, ChessPosition kingPos) {
        ChessPiece attacker = pieces[row][col];
        if (attacker != null && attacker.getTeamColor() != team) {
            for (ChessMove move : attacker.pieceMoves(this, ChessPosition.getPosition(row+1,col+1))) {
                if (move.getEndPosition().equals(kingPos)) {
                    return true;
                }
            }
        }
        return false;
    }

    public ChessPiece[][] getPieces() {
        return pieces;
    }
}
