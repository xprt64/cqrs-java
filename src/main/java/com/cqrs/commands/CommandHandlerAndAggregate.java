package com.cqrs.commands;

import com.cqrs.base.Aggregate;

public class CommandHandlerAndAggregate {

    public final CommandHandlerDescriptor commandHandler;
    public final Aggregate aggregate;

    public CommandHandlerAndAggregate(CommandHandlerDescriptor commandHandler,
        Aggregate aggregate)
    {
        this.commandHandler = commandHandler;
        this.aggregate = aggregate;
    }
}
