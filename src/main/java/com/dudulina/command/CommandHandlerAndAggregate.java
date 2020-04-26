package com.dudulina.command;

import com.dudulina.base.Aggregate;

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
