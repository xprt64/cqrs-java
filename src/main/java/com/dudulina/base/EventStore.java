package com.dudulina.base;

import com.dudulina.aggregates.AggregateDescriptor;
import com.dudulina.event_store.AggregateEventStream;
import com.dudulina.event_store.SeekableEventStream;
import com.dudulina.events.EventWithMetaData;
import java.util.List;

public interface EventStore {

        public AggregateEventStream loadEventsForAggregate(AggregateDescriptor $aggregateDescriptor);

        public void appendEventsForAggregate(AggregateDescriptor $aggregateDescriptor, List<EventWithMetaData> $eventsWithMetaData, AggregateEventStream $expectedEventStream);

        public SeekableEventStream loadEventsByClassNames(List<String> eventClasses);

        public int getAggregateVersion(AggregateDescriptor aggregateDescriptor);

        public EventWithMetaData findEventById(String eventId);
}
