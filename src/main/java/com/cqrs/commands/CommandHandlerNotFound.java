package com.cqrs.commands;

public class CommandHandlerNotFound extends Exception {

    public CommandHandlerNotFound(String message)
    {
        super(message);
    }
}
