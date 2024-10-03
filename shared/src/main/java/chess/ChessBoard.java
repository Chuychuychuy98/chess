package chess;

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
    private final Map<ChessPosition, ChessPiece> pieces = new HashMap<>();

    public ChessBoard() {
        
    }

    public ChessBoard copyOf() {
        ChessBoard chessBoard = new ChessBoard();
        for (ChessPosition position : pieces.keySet()) {
            chessBoard.pieces.put(position, pieces.get(position));
        }
        return chessBoard;
    }

    public void copyBoard(ChessBoard copy) {
        pieces.clear();
        for (ChessPosition position : copy.pieces.keySet()) {
            pieces.put(position, copy.pieces.get(position));
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
        pieces.remove(position);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return Objects.equals(pieces, that.pieces);
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
                sb.append(pieces.get(cur) != null ? pieces.get(cur).toString() : " ");
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
        pieces.put(position, piece);
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return pieces.get(position);
    }

    /**
     * Places a row of non-pawn pieces on a given row for a given color. Assumes normal chess setup.
     * @param row The row to place the pieces on.
     * @param color The color of the pieces.
     */
    private void placeSpecialPieces(int row, ChessGame.TeamColor color) {
        pieces.put(ChessPosition.getPosition(row, 1), new ChessPiece(color, ChessPiece.PieceType.ROOK));
        pieces.put(ChessPosition.getPosition(row, 2), new ChessPiece(color, ChessPiece.PieceType.KNIGHT));
        pieces.put(ChessPosition.getPosition(row, 3), new ChessPiece(color, ChessPiece.PieceType.BISHOP));
        pieces.put(ChessPosition.getPosition(row, 4), new ChessPiece(color, ChessPiece.PieceType.QUEEN));
        pieces.put(ChessPosition.getPosition(row, 5), new ChessPiece(color, ChessPiece.PieceType.KING));
        pieces.put(ChessPosition.getPosition(row, 6), new ChessPiece(color, ChessPiece.PieceType.BISHOP));
        pieces.put(ChessPosition.getPosition(row, 7), new ChessPiece(color, ChessPiece.PieceType.KNIGHT));
        pieces.put(ChessPosition.getPosition(row, 8), new ChessPiece(color, ChessPiece.PieceType.ROOK));
    }

    /**
     * Places pawns of a specified color on the specified row.
     * @param row The row to place the pawns on.
     * @param color The color of the pawns.
     */
    private void placePawns(int row, ChessGame.TeamColor color) {
        for (int i = 1; i <= 8; i++) {
            pieces.put(ChessPosition.getPosition(row, i), new ChessPiece(color, ChessPiece.PieceType.PAWN));
        }
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        placeSpecialPieces(1, ChessGame.TeamColor.WHITE);
        placeSpecialPieces(8, ChessGame.TeamColor.BLACK);

        placePawns(2, ChessGame.TeamColor.WHITE);
        placePawns(7, ChessGame.TeamColor.BLACK);
    }

    /**
     * Returns whether a given position is occupied by an enemy piece.
     * @param pos The position to check.
     * @param color The friendly piece's color.
     * @return Whether the given position is occupied by an enemy piece.
     */
    public boolean positionBlockedByEnemy(ChessPosition pos, ChessGame.TeamColor color) {
        ChessPiece piece = getPiece(pos);
        if (piece == null) return false;
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
        for (ChessPosition pos : pieces.keySet()) {
            ChessPiece piece = pieces.get(pos);
            if (piece != null && piece.getTeamColor() == team && piece.getPieceType() == ChessPiece.PieceType.KING) {
                kingPos = pos;
                break;
            }
        }

        for (ChessPosition pos : pieces.keySet()) {
            ChessPiece attacker = pieces.get(pos);
            if (attacker != null && attacker.getTeamColor() != team) {
                for (ChessMove move : attacker.pieceMoves(this, pos)) {
                    if (move.getEndPosition().equals(kingPos)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
