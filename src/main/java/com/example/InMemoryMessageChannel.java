package com.example;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * In-memory implementation of MessageChannel for same-process communication.
 * Thread-safety: This implementation is thread-safe and supports concurrent send operations from multiple threads.
 */
public class InMemoryMessageChannel implements MessageChannel {
    private final MessageRouter router;
    private final String targetPlayerId;
    private final BlockingQueue<Message> messageQueue;
    private final AtomicBoolean running;
    private Thread processingThread;

    /**
     * Creates new in-memory message channel that routes messages through router.
     */
    public InMemoryMessageChannel(MessageRouter router, String targetPlayerId) {
        this.router = router;
        this.targetPlayerId = targetPlayerId;
        this.messageQueue = new LinkedBlockingQueue<>();
        this.running = new AtomicBoolean(false);
    }

    @Override
    public void send(Message message) {
        if (!running.get()) {
            throw new IllegalStateException("Channel is not running");
        }
        try {
            messageQueue.put(message);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while sending message", e);
        }
    }

    @Override
    public void start() {
        if (running.getAndSet(true)) {
            throw new IllegalStateException("Channel is already running");
        }

        processingThread = new Thread(() -> {
            try {
                while (running.get() || !messageQueue.isEmpty()) {
                    Message message = messageQueue.take();
                    if (message.isStopMessage()) {
                        break;
                    }
                    router.route(message);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "InMemoryChannel-" + targetPlayerId);

        processingThread.start();
    }

    @Override
    public void stop() {
        if (!running.getAndSet(false)) {
            return; // Already stopped
        }

        // Send stop message to wake up processing thread
        try {
            messageQueue.put(Message.stopMessage("system"));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Wait for processing thread to finish
        if (processingThread != null) {
            try {
                processingThread.join(1000); // Wait up to 1 second
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public boolean isRunning() {
        return running.get();
    }
}
