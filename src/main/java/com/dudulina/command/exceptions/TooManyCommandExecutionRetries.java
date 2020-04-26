package com.dudulina.command.exceptions;

public class TooManyCommandExecutionRetries extends Exception {

    public TooManyCommandExecutionRetries(String message)
    {
        super(message);
    }
}
