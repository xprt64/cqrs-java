package com.dudulina.command;

import com.dudulina.aggregates.AggregateDescriptor;
import com.dudulina.events.EventWithMetaData;
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
