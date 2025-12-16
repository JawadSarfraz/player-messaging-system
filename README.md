# Player Messaging System

Messaging system between two players running in the same Java process.

## Building

```bash
mvn clean package
```

## Running

```bash
mvn clean compile
java -cp target/classes com.example.Main
```

## How It Works

1. **Initiator** sends first message "1" to responder
2. **Responder** receives "1", increments it to "2", and sends "2" back
3. **Initiator** receives "2", increments it to "3", and sends "3" to responder
4. This continues: 1 → 2 → 3 → 4 → 5 → 6 → 7 → 8 → 9 → 10 → 11...
5. The program stops when initiator has sent **10 messages** AND received **10 responses**
6. Both players shut down gracefully

## Project Structure

```
src/main/java/com/example/
├── Main.java                  # Entry point
├── Player.java                # Player logic with message handling
├── Message.java               # Message data structure
├── MessageChannel.java        # Transport abstraction
├── InMemoryMessageChannel.java # In-memory communication
├── GameController.java        # Orchestrates the game
├── MessageRouter.java         # Routes messages between players
└── PlayerRole.java            # INITIATOR/RESPONDER enum
```

## Key Features

- Both players run in the same JVM (single process)
- Asynchronous message processing using threads and queues
- Thread-safe implementation with atomic counters

## Testing

```bash
mvn test
```