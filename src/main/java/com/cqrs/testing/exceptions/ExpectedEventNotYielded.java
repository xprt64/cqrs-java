package com.cqrs.testing.exceptions;

public class ExpectedEventNotYielded extends RuntimeException
{
    public ExpectedEventNotYielded(String message)
    {
        super(message);
    }
}
