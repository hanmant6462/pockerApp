package com.sap.ase.poker.model.deck;

public class OutOfCardsException extends RuntimeException {
    public OutOfCardsException(String message) {
        super(message);
    }
}
