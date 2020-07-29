package com.cqrs.commands;

import com.cqrs.annotations.MessageHandler;
import com.cqrs.base.Aggregate;

public class CommandHandlerAndAggregate {

    public final MessageHandler  commandHandler;
    public final Aggregate aggregate;

    public CommandHandlerAndAggregate(
        MessageHandler  commandHandler,
        Aggregate aggregate)
    {
        this.commandHandler = commandHandler;
        this.aggregate = aggregate;
    }
}
