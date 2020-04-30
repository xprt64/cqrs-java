package com.cqrs.commands.exceptions;

public class TooManyCommandExecutionRetries extends Exception {

    public TooManyCommandExecutionRetries(String message)
    {
        super(message);
    }
}
