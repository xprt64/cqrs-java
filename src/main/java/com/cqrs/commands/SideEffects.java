package com.cqrs.commands;

import com.cqrs.aggregates.AggregateDescriptor;
import com.cqrs.events.EventWithMetaData;
import java.util.List;

public class SideEffects {
    public final AggregateDescriptor aggregateDescriptor;
    public final List<EventWithMetaData> events;

    public SideEffects(AggregateDescriptor aggregateDescriptor,
        List<EventWithMetaData> events)
    {
        this.aggregateDescriptor = aggregateDescriptor;
        this.events = events;
    }
}
