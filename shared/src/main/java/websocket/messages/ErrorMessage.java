package websocket.messages;

public class ErrorMessage extends ServerMessage {
    private final String msg;

    public ErrorMessage(String msg) {
        super(ServerMessageType.ERROR);
        this.msg = msg;
    }

    public String getMessage() {
        return msg;
    }
}
