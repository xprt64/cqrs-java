package com.dudulina.base;

import com.dudulina.aggregates.AggregateDescriptor;
import com.dudulina.event_store.AggregateEventStream;
import com.dudulina.event_store.SeekableEventStream;
import com.dudulina.events.EventWithMetaData;
import java.util.ConcurrentModificationException;
import java.util.List;

public interface EventStore {

    AggregateEventStream loadEventsForAggregate(AggregateDescriptor aggregateDescriptor);

    void appendEventsForAggregate(AggregateDescriptor aggregateDescriptor, List<EventWithMetaData> eventsWithMetaData, int expectedVersion)
            throws ConcurrentModificationException;

    SeekableEventStream loadEventsByClassNames(List<String> eventClasses);

    int getAggregateVersion(AggregateDescriptor aggregateDescriptor);

    EventWithMetaData findEventById(String eventId);
}
