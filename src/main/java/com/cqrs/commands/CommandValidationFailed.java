package com.cqrs.commands;

import java.util.List;
import java.util.stream.Collectors;

public class CommandValidationFailed extends Exception {
    private final List<Throwable> errors;

    public CommandValidationFailed(List<Throwable> errors)
    {
        this.errors = errors;
    }

    @Override
    public String toString()
    {
        return errors.stream().map(Throwable::toString).collect(Collectors.joining(", "));
    }

    public List<Throwable> getErrors()
    {
        return errors;
    }
}
