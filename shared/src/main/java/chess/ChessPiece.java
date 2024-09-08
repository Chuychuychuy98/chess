package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final ChessPiece.PieceType type;

    public boolean isMovedBefore() {
        return movedBefore;
    }

    public void setMovedBefore(boolean movedBefore) {
        this.movedBefore = movedBefore;
    }

    private boolean movedBefore = false;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        return String.format("%s %s", pieceColor, type);
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return switch (type) {
            case KING, QUEEN, BISHOP -> null;
            case KNIGHT -> knightMoves(board, myPosition);
            case ROOK -> rookMoves(board, myPosition);
            case PAWN -> pawnMoves(board, myPosition);
        };
    }



    /**
     * Returns a collection containing a knight's potential moves
     * @param board The board being moved on
     * @param myPosition The knight's original position
     * @return A new collection containing a knight's potential moves
     */
    private Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new HashSet<>();
        ChessPosition newPosition = myPosition.translate(1, 2);
        if (newPosition.inBounds() && board.canMoveOrCapture(newPosition, pieceColor)) moves.add(new ChessMove(myPosition, newPosition, null));
        newPosition = myPosition.translate(-1, 2);
        if (newPosition.inBounds() && board.canMoveOrCapture(newPosition, pieceColor)) moves.add(new ChessMove(myPosition, newPosition, null));
        newPosition = myPosition.translate(1, -2);
        if (newPosition.inBounds() && board.canMoveOrCapture(newPosition, pieceColor)) moves.add(new ChessMove(myPosition, newPosition, null));
        newPosition = myPosition.translate(-1, -2);
        if (newPosition.inBounds() && board.canMoveOrCapture(newPosition, pieceColor)) moves.add(new ChessMove(myPosition, newPosition, null));
        newPosition = myPosition.translate(2, 1);
        if (newPosition.inBounds() && board.canMoveOrCapture(newPosition, pieceColor)) moves.add(new ChessMove(myPosition, newPosition, null));
        newPosition = myPosition.translate(-2, 1);
        if (newPosition.inBounds() && board.canMoveOrCapture(newPosition, pieceColor)) moves.add(new ChessMove(myPosition, newPosition, null));
        newPosition = myPosition.translate(2, -1);
        if (newPosition.inBounds() && board.canMoveOrCapture(newPosition, pieceColor)) moves.add(new ChessMove(myPosition, newPosition, null));
        newPosition = myPosition.translate(-2, -1);
        if (newPosition.inBounds() && board.canMoveOrCapture(newPosition, pieceColor)) moves.add(new ChessMove(myPosition, newPosition, null));
        return moves;
    }

    /**
     * Returns a collection of a rook's possible moves
     * @param board The board being moved on
     * @param myPosition The rook's starting position
     * @return A new collection of the rook's potential moves
     */
    private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new HashSet<>();
        addRookMovesUpDown(board, myPosition, true, moves);
        addRookMovesUpDown(board, myPosition, false, moves);
        addRookMovesLeftRight(board, myPosition, true, moves);
        addRookMovesLeftRight(board, myPosition, false, moves);
        return moves;
    }

    /**
     * Add vertical moves to a rook's potential move set
     * @param board The board being moved on
     * @param myPosition The rook's starting position
     * @param up Whether the rook is moving up or down
     * @param moves The move set being added to
     */
    private void addRookMovesUpDown(ChessBoard board, ChessPosition myPosition, boolean up, Collection<ChessMove> moves) {
        int spaces = 1;
        ChessPosition newPosition = myPosition.translate(up ? spaces : -spaces, 0);
        while (newPosition.inBounds() && board.getPiece(newPosition) == null) {
            moves.add(new ChessMove(myPosition, newPosition, null));
            spaces++;
            newPosition = myPosition.translate(up ? spaces : -spaces, 0);
        }
    }

    /**
     * Add horizontal moves to a rook's potential move set
     * @param board The board being moved on
     * @param myPosition The rook's starting position
     * @param left Whether the rook is moving left or right
     * @param moves The move set being added to
     */
    private void addRookMovesLeftRight(ChessBoard board, ChessPosition myPosition, boolean left, Collection<ChessMove> moves) {
        int spaces = 1;
        ChessPosition newPosition = myPosition.translate(0, left ? -spaces : spaces);
        while (newPosition.inBounds() && board.getPiece(newPosition) == null) {
            moves.add(new ChessMove(myPosition, newPosition, null));
            spaces++;
            newPosition = myPosition.translate(0, left ? -spaces : spaces);
        }
    }

    /**
     * Add pawn moves to a collection of moves, including all possible promotions.
     * @param oldPos The position being moved from.
     * @param newPos The position being moved to.
     * @param moves The collection to add the moves to.
     */
    private void addPawnMoveWithPromotions(ChessPosition oldPos, ChessPosition newPos, Collection<ChessMove> moves) {
        if ((newPos.getRow() == 0 && pieceColor == ChessGame.TeamColor.BLACK) || (newPos.getRow() == 7 && pieceColor == ChessGame.TeamColor.WHITE)) {
            moves.add(new ChessMove(oldPos, newPos, ChessPiece.PieceType.QUEEN));
            moves.add(new ChessMove(oldPos, newPos, ChessPiece.PieceType.BISHOP));
            moves.add(new ChessMove(oldPos, newPos, ChessPiece.PieceType.KNIGHT));
            moves.add(new ChessMove(oldPos, newPos, ChessPiece.PieceType.ROOK));
        }
        else {
            moves.add(new ChessMove(oldPos, newPos, null));
        }
    }

    /**
     * Returns a collection of a pawn's possible moves.
     * @param board The board the piece is on.
     * @param curPos The current position.
     * @return A collection of all possible moves.
     */
    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition curPos) {
        Collection<ChessMove> moves = new HashSet<>();

        ChessPosition newPos = curPos.forward(pieceColor);
        if (!newPos.inBounds()) return moves;

        if (board.getPiece(newPos) == null) {
            addPawnMoveWithPromotions(curPos, newPos, moves);
            if (!movedBefore) {
                newPos = newPos.forward(pieceColor);
                if (newPos.inBounds() && board.getPiece(newPos) == null) {
                    addPawnMoveWithPromotions(curPos, newPos, moves);
                }
            }
        }

        newPos = curPos.translate(pieceColor, 1, 1);
        if (board.positionBlockedByEnemy(newPos, pieceColor)) {
            moves.add(new ChessMove(curPos, newPos, null));
        }
        newPos = curPos.translate(pieceColor, 1, -1);
        if (board.positionBlockedByEnemy(newPos, pieceColor)) {
            moves.add(new ChessMove(curPos, newPos, null));
        }

        return moves;
    }
}
