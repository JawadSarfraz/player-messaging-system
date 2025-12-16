package com.example;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a player in messaging system that can send and receive messages.
 */
public class Player {
    private final String playerId;
    private final PlayerRole role;
    private final MessageChannel messageChannel;
    private final AtomicInteger sentCount;

    // Only used by INITIATOR to track stop condition
    private final AtomicInteger messagesSent;
    private final AtomicInteger responsesReceived;
    private final Runnable onStopConditionMet;

    // Flag to prevent sending messages after shutdown
    private volatile boolean stopped;

    /**
     * Creates new Player instance.
     */
    public Player(String playerId, PlayerRole role, MessageChannel messageChannel, Runnable onStopConditionMet) {
        this.playerId = playerId;
        this.role = role;
        this.messageChannel = messageChannel;
        this.sentCount = new AtomicInteger(0);
        this.messagesSent = new AtomicInteger(0);
        this.responsesReceived = new AtomicInteger(0);
        this.onStopConditionMet = onStopConditionMet;
    }

    /**
     * Gets unique identifier of this player.
     */
    public String getPlayerId() {
        return playerId;
    }

    /**
     * Gets role of this player.
     */
    public PlayerRole getRole() {
        return role;
    }

    /**
     * Gets current count of messages sent by this player.
     */
    public int getSentCount() {
        return sentCount.get();
    }

    /**
     * Sends message through this player's message channel.
     */
    public void send(String payload, String toPlayerId) {
        int currentCount = sentCount.incrementAndGet();

        if (role == PlayerRole.INITIATOR) {
            messagesSent.incrementAndGet();
        }

        Message message = new Message(payload, playerId, toPlayerId, currentCount);
        messageChannel.send(message);

        System.out.println(String.format("[%s] Sent message #%d to %s: %s",
                playerId, currentCount, toPlayerId, payload));
    }

    /**
     * Handles an incoming message.
     */
    public void onMessage(Message message) {
        System.out.println(String.format("[%s] Received message from %s: %s",
                playerId, message.getFromPlayerId(), message.getPayload()));

        // Don't send response if player is already stopped (prevents race condition
        // during shutdown)
        if (stopped) {
            return;
        }

        // Build response: extract number from received message and increment it
        // Expected format: just a number (e.g., "1", "2", "3", etc.)
        String receivedPayload = message.getPayload();
        int nextNumber = 1;

        try {
            int receivedNumber = Integer.parseInt(receivedPayload);
            nextNumber = receivedNumber + 1; // Increment number
        } catch (NumberFormatException e) {
            // If parsing fails, start from 1
            nextNumber = 1;
        }

        String responsePayload = String.valueOf(nextNumber);

        // Send response back to sender (only if not stopped)
        if (!stopped) {
            send(responsePayload, message.getFromPlayerId());
        }

        // If this is the initiator, check stop condition
        if (role == PlayerRole.INITIATOR) {
            int received = responsesReceived.incrementAndGet();
            int sent = messagesSent.get();

            System.out.println(String.format("[%s] Progress: sent=%d, received=%d",
                    playerId, sent, received));

            if (sent >= 10 && received >= 10) {
                System.out.println(String.format("[%s] Stop condition met! Sending STOP signal.", playerId));
                if (onStopConditionMet != null) {
                    onStopConditionMet.run();
                }
            }
        }
    }

    /**
     * Starts this player's message channel.
     */
    public void start() {
        messageChannel.start();
    }

    /**
     * Stops this player's message channel.
     */
    public void stop() {
        stopped = true; // Set flag first to prevent new messages during shutdown
        messageChannel.stop();
    }
}