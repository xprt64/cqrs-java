package com.dudulina.events;

import com.dudulina.base.Aggregate;
import com.dudulina.command.CommandWithMetadata;
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
