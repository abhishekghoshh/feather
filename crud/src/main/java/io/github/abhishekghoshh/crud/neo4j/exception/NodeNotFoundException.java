package io.github.abhishekghoshh.crud.neo4j.exception;

public class NodeNotFoundException extends Exception {
    String message;

    public NodeNotFoundException(String message) {
        super(message);
        this.message = message;
    }
}
