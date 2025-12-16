package com.example;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Unit tests for MessageRouter class.
 */
public class MessageRouterTest {

    @Test
    public void testRouteMessage() throws InterruptedException {
        MessageRouter router = new MessageRouter();
        CountDownLatch messageReceived = new CountDownLatch(1);

        // Create player
        Player player = new Player("player1", PlayerRole.RESPONDER,
                new InMemoryMessageChannel(router, "sender"), null) {
            @Override
            public void onMessage(Message message) {
                // Override to just receive, not send response
                System.out.println(String.format("[%s] Received message from %s: %s",
                        getPlayerId(), message.getFromPlayerId(), message.getPayload()));
                messageReceived.countDown();
            }
        };

        router.register("player1", player);

        Message msg = new Message("1", "sender", "player1");
        router.route(msg);

        // Wait for message to be processed (router.route is synchronous)
        assertTrue("Message should be routed immediately",
                messageReceived.await(1, TimeUnit.SECONDS));
    }
}