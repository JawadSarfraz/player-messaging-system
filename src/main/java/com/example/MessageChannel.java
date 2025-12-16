package com.example;

/**
 * Abstraction for message delivery between Player instances.
 */
public interface MessageChannel {
    /**
     * Sends a message through this channel.
     */
    void send(Message message);

    /**
     * Starts the message channel, initializing any necessary resources (threads, sockets, etc.) and beginning to process messages.
     */
    void start();

    /**
     * Stops the message channel, gracefully shutting down resources. After calling stop(), the channel should not accept new messages.
     */
    void stop();

    /**
     * Checks if the channel is currently running.
     */
    boolean isRunning();
}