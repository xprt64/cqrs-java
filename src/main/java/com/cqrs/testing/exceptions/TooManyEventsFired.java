package com.cqrs.testing.exceptions;

public class TooManyEventsFired extends Exception
{
    public TooManyEventsFired(String message)
    {
        super(message);
    }
}
