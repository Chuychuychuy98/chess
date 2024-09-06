package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    private final int row;
    private final int col;

    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Returns a new position considered moving "forward" by a number of spaces, based on the piece's color.
     * @param color The color of the moving piece.
     * @param spaces The number of spaces to move.
     * @return The position reached by moving forward the given number of spaces.
     */
    public ChessPosition forward(ChessGame.TeamColor color, int spaces) {
        return new ChessPosition(color.equals(ChessGame.TeamColor.BLACK) ? row - spaces : row + spaces, col);
    }

    /**
     * Returns a new position considered moving "forward" by one space, based on the piece's color.
     * @param color The color of the moving piece.
     * @return The position reached by moving forward one space.
     */
    public ChessPosition forward(ChessGame.TeamColor color) {
        return forward(color, 1);
    }

    /**
     * Returns a new position representing a translation of the current position. Row change is added or subtracted based on color.
     * @param color The color of the piece.
     * @param rowChange The amount to change the row by.
     * @param colChange The amount to change the column by.
     * @return The new position.
     */
    public ChessPosition translate(ChessGame.TeamColor color, int rowChange, int colChange) {
        if (color == ChessGame.TeamColor.BLACK) {
            return new ChessPosition(row - rowChange, col + colChange);
        }
        else {
            return new ChessPosition(row + rowChange, col + colChange);
        }
    }

    /**
     * Returns whether the position is within the given bounds (inclusive).
     * @param rowMin Minimum allowed row value.
     * @param rowMax Maximum allowed row value.
     * @param colMin Minimum allowed column value.
     * @param colMax Maximum allowed colum value.
     * @return Whether the position is within the given bounds (inclusive).
     */
    public boolean inBounds(int rowMin, int rowMax, int colMin, int colMax) {
        return row >= rowMin && row <= rowMax && col >= colMin && col <= colMax;
    }

    /**
     * Returns whether the position is within normal chess bounds (1 - 8)
     * @return Whether this position's row and column are between 1 - 8 inclusive.
     */
    public boolean inBounds() {
        return inBounds(1, 8, 1, 8);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPosition that = (ChessPosition) o;
        return row == that.row && col == that.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return col;
    }

    @Override
    public String toString() {
        return "(" + row + ", " + col + ")";
    }
}
