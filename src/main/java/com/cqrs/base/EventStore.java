package com.cqrs.base;

import com.cqrs.aggregates.AggregateDescriptor;
import com.cqrs.event_store.SeekableEventStream;
import com.cqrs.event_store.exceptions.StorageException;
import com.cqrs.events.EventWithMetaData;

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.function.Predicate;

public interface EventStore {

    int loadEventsForAggregate(AggregateDescriptor aggregateDescriptor, Predicate<EventWithMetaData> consumer) throws StorageException;

    void appendEventsForAggregate(AggregateDescriptor aggregateDescriptor, List<EventWithMetaData> eventsWithMetaData, int expectedVersion)
            throws ConcurrentModificationException, StorageException;

    void loadEventsByClassNames(List<String> eventClasses, Predicate<EventWithMetaData> consumer) throws StorageException;
    int countEventsByClassNames(List<String> eventClasses) throws StorageException;

    EventWithMetaData findEventById(String eventId) throws StorageException;
}
