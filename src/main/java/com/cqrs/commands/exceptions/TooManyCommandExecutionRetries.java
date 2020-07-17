package com.cqrs.commands.exceptions;

public class TooManyCommandExecutionRetries extends RuntimeException {

    public TooManyCommandExecutionRetries(String message)
    {
        super(message);
    }
}
