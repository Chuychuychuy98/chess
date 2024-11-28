package server;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.NotificationMessage;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, Set<Connection>> connections = new ConcurrentHashMap<>();

    public void add(int gameID, String username, Session session) {
        connections.computeIfAbsent(gameID, k -> new HashSet<>()).add(new Connection(username, session));
    }

    public void remove(String visitorName) {
        connections.forEach((gameID, set) -> set.removeIf(conn -> conn.username().equals(visitorName)));
    }

    public void broadcast(int gameID, NotificationMessage notification) throws IOException {
        broadcast(gameID, notification, null);
    }

    public void broadcast(int gameID, NotificationMessage notification, String excludeUsername) throws IOException {
        Set<Connection> removeSet = new HashSet<>();
        Set<Connection> connectionsByID = connections.get(gameID);
        for (Connection conn : connectionsByID) {
            if (conn.session().isOpen()) {
                if (!conn.username().equals(excludeUsername)) {
                    conn.send(notification);
                }
            }
            else {
                removeSet.add(conn);
            }
        }
        connectionsByID.removeIf(removeSet::contains);
        if (connectionsByID.isEmpty()) {
            connections.remove(gameID);
        }
    }
}
