package com.cqrs.testing.exceptions;

public class NoExceptionThrown extends AssertionError
{
    public NoExceptionThrown(String message)
    {
        super(message);
    }
}
