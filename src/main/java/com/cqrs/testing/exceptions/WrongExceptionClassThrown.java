package com.cqrs.testing.exceptions;

public class WrongExceptionClassThrown extends AssertionError
{
    public WrongExceptionClassThrown(String message)
    {
        super(message);
    }
}
