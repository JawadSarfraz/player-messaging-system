package com.example;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Unit tests for InMemoryMessageChannel class.
 */
public class InMemoryMessageChannelTest {

    @Test
    public void testMessageDelivery() throws InterruptedException {
        MessageRouter router = new MessageRouter();
        CountDownLatch messageReceived = new CountDownLatch(1);

        // Create mock player that counts down when it receives a message
        Player mockPlayer = new Player("target", PlayerRole.RESPONDER,
                new InMemoryMessageChannel(router, "sender"), null) {
            @Override
            public void onMessage(Message message) {
                super.onMessage(message);
                messageReceived.countDown();
            }
        };

        router.register("target", mockPlayer);

        InMemoryMessageChannel channel = new InMemoryMessageChannel(router, "target");
        mockPlayer.start();
        channel.start();

        Message msg = new Message("1", "sender", "target");
        channel.send(msg);

        // Wait for message to be delivered
        assertTrue("Message should be delivered within 1 second",
                messageReceived.await(1, TimeUnit.SECONDS));

        channel.stop();
        mockPlayer.stop();
    }
}
