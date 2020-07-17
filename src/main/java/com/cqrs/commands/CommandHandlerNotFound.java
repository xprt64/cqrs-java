package com.cqrs.commands;

public class CommandHandlerNotFound extends RuntimeException {

    public CommandHandlerNotFound(String message)
    {
        super(message);
    }
}
