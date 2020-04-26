package com.dudulina.command.exceptions;

public class CommandExecutionFailed extends RuntimeException
{
    public CommandExecutionFailed(Throwable cause)
    {
        super(cause);
    }

    public CommandExecutionFailed(String message, Throwable cause)
    {
        super(message, cause);
    }
}
