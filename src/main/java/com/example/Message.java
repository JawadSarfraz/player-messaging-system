package com.example;

/**
 * Represents a message exchanged between Player instances.
 * For network communication, messages are serialized to a line-based format.
 */
public final class Message {
    private final String payload;
    private final String fromPlayerId;
    private final String toPlayerId;
    private final Integer sequenceNumber;

    /**
     * Creates a new message.
     */
    public Message(String payload, String fromPlayerId, String toPlayerId, Integer sequenceNumber) {
        this.payload = payload;
        this.fromPlayerId = fromPlayerId;
        this.toPlayerId = toPlayerId;
        this.sequenceNumber = sequenceNumber;
    }

    /**
     * Creates a message without sequence number.
     */
    public Message(String payload, String fromPlayerId, String toPlayerId) {
        this(payload, fromPlayerId, toPlayerId, null);
    }

    public String getPayload() {
        return payload;
    }

    public String getFromPlayerId() {
        return fromPlayerId;
    }

    public String getToPlayerId() {
        return toPlayerId;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    /**
     * Serializes this message to line-based string format for network transmission.
     * Format: fromPlayerId|toPlayerId|sequenceNumber|payload
     * 
     * @return serialized message string
     */
    public String serialize() {
        StringBuilder sb = new StringBuilder();
        sb.append(fromPlayerId != null ? fromPlayerId : "");
        sb.append("|");
        sb.append(toPlayerId != null ? toPlayerId : "");
        sb.append("|");
        sb.append(sequenceNumber != null ? sequenceNumber : "");
        sb.append("|");
        sb.append(payload != null ? payload : "");
        return sb.toString();
    }

    /**
     * Deserializes a message from a line-based string format.
     */
    public static Message deserialize(String serialized) {
        if (serialized == null || serialized.trim().isEmpty()) {
            throw new IllegalArgumentException("Serialized message cannot be null or empty");
        }

        String[] parts = serialized.split("\\|", -1);
        if (parts.length < 4) {
            throw new IllegalArgumentException("Invalid message format: " + serialized);
        }

        String from = parts[0].isEmpty() ? null : parts[0];
        String to = parts[1].isEmpty() ? null : parts[1];
        Integer seq = parts[2].isEmpty() ? null : Integer.parseInt(parts[2]);
        String payload = parts[3];

        return new Message(payload, from, to, seq);
    }

    /**
     * Special message type to signal shutdown.
     */
    public static Message stopMessage(String fromPlayerId) {
        return new Message("STOP", fromPlayerId, null, null);
    }

    /**
     * Checks if this is a stop message.
     */
    public boolean isStopMessage() {
        return "STOP".equals(payload);
    }

    @Override
    public String toString() {
        return String.format("Message{from=%s, to=%s, seq=%s, payload='%s'}", 
            fromPlayerId, toPlayerId, sequenceNumber, payload);
    }
}

