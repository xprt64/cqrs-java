package com.cqrs.testing.exceptions;

public class WrongExceptionMessageWasThrown extends AssertionError
{
    public WrongExceptionMessageWasThrown(String message)
    {
        super(message);
    }
}
