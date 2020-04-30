package com.cqrs.testing.exceptions;

public class ExpectedEventNotYielded extends Exception
{
    public ExpectedEventNotYielded(String message)
    {
        super(message);
    }
}
