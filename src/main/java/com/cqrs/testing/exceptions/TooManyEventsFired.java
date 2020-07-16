package com.cqrs.testing.exceptions;

public class TooManyEventsFired extends AssertionError
{
    public TooManyEventsFired(String message)
    {
        super(message);
    }
}
