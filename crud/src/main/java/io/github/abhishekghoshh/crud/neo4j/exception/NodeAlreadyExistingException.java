package io.github.abhishekghoshh.crud.neo4j.exception;

public class NodeAlreadyExistingException extends Exception {
    String message;

    public NodeAlreadyExistingException(String message) {
        super(message);
        this.message = message;
    }
}
