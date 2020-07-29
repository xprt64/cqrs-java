package com.cqrs.aggregates;

import com.cqrs.base.Aggregate;
import com.cqrs.base.Command;

public class AggregateCommandHandlingException extends RuntimeException {

    public final Aggregate aggregate;
    private final Command command;
    private final String methodName;

    public AggregateCommandHandlingException(Aggregate aggregate, Command command, String methodName, Throwable cause) {
        super(cause);
        this.aggregate = aggregate;
        this.command = command;
        this.methodName = methodName;
    }

    @Override
    public String toString() {
        return "Aggregate " + aggregate.getClass().getCanonicalName() + " threw " + getCause()
            .getClass().getCanonicalName() + " while handing command of type " + command.getClass().getCanonicalName() + " in method " + methodName + "; the message is: " + getCause().getMessage();
    }
}
