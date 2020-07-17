package com.cqrs.testing.exceptions;

public class WrongEventClassYielded extends AssertionError
{
    public WrongEventClassYielded(String message)
    {
        super(message);
    }
}
