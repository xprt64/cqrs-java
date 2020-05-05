package com.cqrs.commands;

import java.util.List;
import java.util.stream.Collectors;

public class CommandRejectedByValidators extends Exception {
    private final List<Throwable> errors;

    public CommandRejectedByValidators(List<Throwable> errors)
    {
        this.errors = errors;
    }

    @Override
    public String toString()
    {
        return errors.stream().map(Throwable::getMessage).collect(Collectors.joining(", "));
    }

    public List<Throwable> getErrors()
    {
        return errors;
    }
}
