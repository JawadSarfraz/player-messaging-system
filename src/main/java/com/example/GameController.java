package com.example;

import java.util.concurrent.CountDownLatch;

/**
 * Orchestrates messaging game between two players.
 */
public class GameController {
    private Player initiator;
    private Player responder;
    private final CountDownLatch stopLatch;
    private volatile boolean stopped;

    /**
     * Creates new GameController.
     */
    public GameController() {
        this.stopLatch = new CountDownLatch(1);
        this.stopped = false;
    }

    /**
     * Initializes and starts the game.
     * Creates both players in same JVM and starts conversation.
     */
    public void start() {
        System.out.println("Starting player messaging system...");

        // Create message router
        MessageRouter router = new MessageRouter();

        // Create message channels
        MessageChannel initiatorChannel = new InMemoryMessageChannel(router, "responder");
        MessageChannel responderChannel = new InMemoryMessageChannel(router, "initiator");

        // Create players with stop condition callback
        Runnable onStop = () -> {
            if (!stopped) {
                stopped = true;
                stopLatch.countDown();
            }
        };

        responder = new Player("responder", PlayerRole.RESPONDER, responderChannel, null);
        initiator = new Player("initiator", PlayerRole.INITIATOR, initiatorChannel, onStop);

        // Register players with router
        router.register("responder", responder);
        router.register("initiator", initiator);

        // Start both players
        responder.start();
        initiator.start();

        // Start the conversation
        System.out.println("Initiator sending first message...");
        initiator.send("1", "responder");

        // Wait for stop condition
        try {
            stopLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        shutdown();
    }

    private void shutdown() {
        System.out.println("Shutting down...");
        if (initiator != null) {
            initiator.stop();
        }
        if (responder != null) {
            responder.stop();
        }
        System.out.println("Shutdown complete");
    }
}