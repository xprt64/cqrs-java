package com.cqrs.testing.exceptions;

public class WrongExceptionMessageWasThrown extends Exception
{
    public WrongExceptionMessageWasThrown(String message)
    {
        super(message);
    }
}
