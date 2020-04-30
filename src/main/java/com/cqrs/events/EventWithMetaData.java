package com.cqrs.events;

import com.cqrs.base.Event;


public class EventWithMetaData {

    public final Event event;
    public final MetaData metadata;

    public EventWithMetaData(Event event, MetaData metadata)
    {
        this.event = event;
        this.metadata = metadata;
    }

    public EventWithMetaData withVersion(int version)
    {
        return new EventWithMetaData(event, metadata.withVersion(version));
    }
}
