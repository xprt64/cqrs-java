package com.cqrs.exceptions_base;

import java.util.List;
import java.util.stream.Collectors;

abstract public class MessageRejectedByValidators extends RuntimeException {
    private final List<Throwable> errors;

    public MessageRejectedByValidators(List<Throwable> errors) {
        this.errors = errors;
    }

    @Override
    public String toString() {
        return errors.stream().map(Throwable::getMessage).collect(Collectors.joining(", "));
    }

    public List<Throwable> getErrors() {
        return errors;
    }

    @Override
    public String getMessage() {
        return toString();
    }
}
