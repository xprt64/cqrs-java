package com.cqrs.events;

import com.cqrs.base.Aggregate;
import com.cqrs.commands.CommandWithMetadata;
import java.time.LocalDateTime;

public interface MetadataFactory {

    default public MetaData factoryEventMetadata(CommandWithMetadata command, Aggregate aggregate)
    {
        return new MetaData(
            LocalDateTime.now(),
            command.command.getAggregateId(),
            aggregate.getClass().getCanonicalName(),
            command.metadata
        );
    }
}
