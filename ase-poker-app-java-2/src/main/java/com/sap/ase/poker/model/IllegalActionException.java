package com.sap.ase.poker.model;

/*
 * This class is internally used to identify illegal operations. Example:
 * checking when there are placed bets. This is an illegal
 * usage from the client, not a server error.
 */
public class IllegalActionException extends RuntimeException {
    public IllegalActionException(String message) {
        super(message);
    }
}