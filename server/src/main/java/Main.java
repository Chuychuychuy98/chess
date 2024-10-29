import chess.*;
import server.Server;

public class Main {
    public static void main(String[] args) {
//        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
//        System.out.println("♕ 240 Chess Server: " + piece);
        Server server;
        try {
            server = new Server();
        }
        catch (Throwable ex) {
            System.out.printf("Unable to start server: %s%n", ex.getMessage());
            return;
        }
        server.run(8080);
    }
}