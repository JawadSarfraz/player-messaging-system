package com.example;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Routes messages between players in the same process.
 * This class is used only in single-process mode to enable communication between players without direct references.
 */
public class MessageRouter {
    private final Map<String, Player> players;

    public MessageRouter() {
        this.players = new ConcurrentHashMap<>();
    }

    /**
     * Registers a player with this router.
     */
    public void register(String playerId, Player player) {
        players.put(playerId, player);
    }

    /**
     * Routes a message to the target player.
     */
    public void route(Message message) {
        if (message.getToPlayerId() == null) {
            return;
        }

        Player target = players.get(message.getToPlayerId());
        if (target != null) {
            target.onMessage(message);
        }
    }

    /**
     * Unregisters a player.
     */
    public void unregister(String playerId) {
        players.remove(playerId);
    }
}