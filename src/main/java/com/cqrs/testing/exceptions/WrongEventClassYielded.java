package com.cqrs.testing.exceptions;

public class WrongEventClassYielded extends Exception
{
    public WrongEventClassYielded(String message)
    {
        super(message);
    }
}
