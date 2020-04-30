package com.cqrs.commands;

import com.cqrs.base.Command;

public class CommandWithMetadata {

    public final Command command;
    public final CommandMetaData metadata;

    public CommandWithMetadata(Command command, CommandMetaData metadata)
    {
        this.command = command;
        this.metadata = metadata;
    }

    public String getAggregateId()
    {
        return command.getAggregateId();
    }
}
