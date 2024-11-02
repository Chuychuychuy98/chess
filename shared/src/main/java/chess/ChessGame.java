package chess;

import java.util.*;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor turn;
    private ChessBoard board;

    public ChessGame() {
        turn = TeamColor.WHITE;
        this.board = ChessBoard.defaultBoard();
    }

    public ChessGame(TeamColor turn, ChessBoard board) {
        this.turn = turn;
        this.board = board;
    }

    public ChessGame(String serializedGame) {
        String turn = serializedGame.substring(9, 14);
        if (turn.equals("WHITE")) {
            this.turn = TeamColor.WHITE;
        }
        else if (turn.equals("BLACK")) {
            this.turn = TeamColor.BLACK;
        }
        else {
            throw new IllegalArgumentException("Error: Game was not serialized properly");
        }
        this.board = new ChessBoard(serializedGame.substring(35, serializedGame.length()-3).split("}"));

    }

    /**
     * Update the elements of this game to match a given new game.
     * @param newGame The game whose elements this game will copy.
     */
    public void updateGame(ChessGame newGame) {
        this.turn = newGame.turn;
        this.board = newGame.board;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece toMove = board.getPiece(startPosition);
        if (toMove == null) {
            return null;
        }
        TeamColor color = toMove.getTeamColor();
        Collection<ChessMove> allMoves = toMove.pieceMoves(board, startPosition);
        Collection<ChessMove> validMoves = new HashSet<>();
        ChessBoard simBoard = board.copyOf();
        for (ChessMove move : allMoves) {
            simBoard.simMove(move);
            if (!simBoard.isInCheck(color)) {
                validMoves.add(move);
            }
            simBoard.copyBoard(board);
        }
        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        Collection<ChessMove> moves = validMoves(move.getStartPosition());
        if (moves != null && moves.contains(move) && board.getPiece(move.getStartPosition()).getTeamColor() == turn) {
            board.doMove(move);
        }
        else {
            throw new InvalidMoveException();
        }
        if (turn == TeamColor.WHITE) {
            turn = TeamColor.BLACK;
        }
        else {
            turn = TeamColor.WHITE;
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return board.isInCheck(teamColor);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false;
        }
        return noValidMoves(teamColor);
    }

    private boolean noValidMoves(TeamColor teamColor) {
        Map<ChessPosition, ChessPiece> pieces = board.getPieces();
        for (ChessPosition pos : pieces.keySet()) {
            if (pieces.get(pos).getTeamColor() == teamColor && !validMoves(pos).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }
        return noValidMoves(teamColor);
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return turn == chessGame.turn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(turn, board);
    }
}
