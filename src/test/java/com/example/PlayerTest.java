package com.example;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Unit tests for the Player class.
 */
public class PlayerTest {

    @Test
    public void testPlayerCreation() {
        MessageChannel channel = new InMemoryMessageChannel(new MessageRouter(), "target");
        Player player = new Player("testPlayer", PlayerRole.INITIATOR, channel, null);

        assertEquals("testPlayer", player.getPlayerId());
        assertEquals(PlayerRole.INITIATOR, player.getRole());
        assertEquals(0, player.getSentCount());
    }

    @Test
    public void testInitiatorSendsMessage() throws InterruptedException {
        MessageRouter router = new MessageRouter();
        MessageChannel channel = new InMemoryMessageChannel(router, "responder");
        CountDownLatch messageReceived = new CountDownLatch(1);

        Player responder = new Player("responder", PlayerRole.RESPONDER,
                new InMemoryMessageChannel(router, "initiator"), null);
        router.register("responder", responder);

        Player initiator = new Player("initiator", PlayerRole.INITIATOR, channel, null);
        router.register("initiator", initiator);

        responder.start();
        initiator.start();

        initiator.send("1", "responder");

        // Wait a bit for message processing
        Thread.sleep(100);

        assertTrue("Initiator should have sent at least 1 message", initiator.getSentCount() >= 1);

        responder.stop();
        initiator.stop();
    }

    @Test
    public void testResponderEchoesMessage() throws InterruptedException {
        MessageRouter router = new MessageRouter();
        CountDownLatch responseReceived = new CountDownLatch(1);

        Player responder = new Player("responder", PlayerRole.RESPONDER,
                new InMemoryMessageChannel(router, "initiator"), null);
        router.register("responder", responder);

        Player initiator = new Player("initiator", PlayerRole.INITIATOR,
                new InMemoryMessageChannel(router, "responder"), null);
        router.register("initiator", initiator);

        responder.start();
        initiator.start();

        initiator.send("1", "responder");

        // Wait for response
        Thread.sleep(200);

        // Responder should have sent a response
        assertTrue("Responder should have sent at least 1 message", responder.getSentCount() >= 1);

        responder.stop();
        initiator.stop();
    }

}
