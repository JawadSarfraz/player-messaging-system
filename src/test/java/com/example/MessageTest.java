package com.example;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for the Message class.
 */
public class MessageTest {

    @Test
    public void testMessageCreation() {
        Message msg = new Message("1", "player1", "player2", 1);
        assertEquals("1", msg.getPayload());
        assertEquals("player1", msg.getFromPlayerId());
        assertEquals("player2", msg.getToPlayerId());
        assertEquals(Integer.valueOf(1), msg.getSequenceNumber());
    }

    @Test
    public void testStopMessage() {
        Message stop = Message.stopMessage("player1");
        assertTrue(stop.isStopMessage());
        assertEquals("STOP", stop.getPayload());
    }
}
