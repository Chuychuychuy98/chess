package websocket.messages;

import com.google.gson.Gson;
import model.GameData;

public class LoadGameMessage extends ServerMessage {
    private final String game;

    public LoadGameMessage(String game) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }

    public String getGameString() {
        return game;
    }

    public GameData getGame() {
        return new Gson().fromJson(game, GameData.class);
    }
}
