package com.example;

/**
 * Enumeration representing role of Player in messaging system.
 */
public enum PlayerRole {
    /**
     * The player that initiates the conversation by sending first message.
     * This player is responsible for tracking when to stop (after 10 sends + 10
     * receives).
     */
    INITIATOR,

    /**
     * The player that responds to messages from initiator.
     * This player simply echoes back messages with its counter appended.
     */
    RESPONDER
}
