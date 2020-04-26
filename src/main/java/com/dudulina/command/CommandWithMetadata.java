package com.dudulina.command;

import com.dudulina.aggregates.AggregateId;
import com.dudulina.base.Command;

public class CommandWithMetadata {

    public final Command command;
    public final CommandMetaData metadata;

    public CommandWithMetadata(Command command, CommandMetaData metadata)
    {
        this.command = command;
        this.metadata = metadata;
    }

    public AggregateId getAggregateId()
    {
        return command.getAggregateId();
    }
}
