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
    private boolean over = false;

    public ChessGame() {
        turn = TeamColor.WHITE;
        this.board = ChessBoard.defaultBoard();
    }

    public ChessGame(TeamColor turn, ChessBoard board) {
        this.turn = turn;
        this.board = board;
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

    public Collection<ChessPosition> validEndPositions(ChessPosition startPosition) {
        return validMoves(startPosition).stream().map(ChessMove::getEndPosition).toList();
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK;

        public TeamColor opposite() {
            if (this.equals(WHITE)) {
                return BLACK;
            }
            else {
                return WHITE;
            }
        }
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
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     * @throws WrongTurnException if the wrong player is trying to move
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (over) {
            throw new GameOverException();
        }
        Collection<ChessMove> moves = validMoves(move.getStartPosition());
        if (moves != null && moves.contains(move)) {
            if (board.getPiece(move.getStartPosition()).getTeamColor() == turn) {
                board.doMove(move);
            }
            else {
                throw new WrongTurnException();
            }
        }
        else {
            throw new InvalidMoveException("You cannot move there.");
        }
        if (turn == TeamColor.WHITE) {
            turn = TeamColor.BLACK;
        }
        else {
            turn = TeamColor.WHITE;
        }
        if (isInStalemate(turn) || isInCheckmate(turn)) {
            over = true;
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
        ChessPiece[][] pieces = board.getPieces();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (pieces[i][j] != null &&pieces[i][j].getTeamColor() == teamColor
                        && !validMoves(ChessPosition.getPosition(i+1,j+1)).isEmpty()) {
                    return false;
                }
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

    public boolean isOver() {
        return over;
    }

    public void setGameOver() throws GameOverException {
        if (over) {
            throw new GameOverException();
        }
        over = true;
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
