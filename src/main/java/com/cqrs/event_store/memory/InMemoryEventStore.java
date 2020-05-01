package com.cqrs.event_store.memory;

import com.cqrs.aggregates.AggregateDescriptor;
import com.cqrs.base.EventStore;
import com.cqrs.events.EventWithMetaData;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public class InMemoryEventStore implements EventStore {

    HashMap<String, InMemoryAggregateEventStream> streams = new HashMap<>();

    @Override
    public int loadEventsForAggregate(AggregateDescriptor aggregateDescriptor, Predicate<EventWithMetaData> consumer) {
        InMemoryAggregateEventStream stream = streams.getOrDefault(aggregateDescriptor.toString(), new InMemoryAggregateEventStream());
        int version = -1;
        while (stream.hasNext()) {
            final EventWithMetaData next = stream.next();
            version = next.metadata.version;
            if (!consumer.test(next)) {
                return version;
            }
        }
        return version;
    }

    @Override
    public void appendEventsForAggregate(
        AggregateDescriptor aggregateDescriptor,
        List<EventWithMetaData> eventsWithMetaData,
        int expectedVersion
    ) throws ConcurrentModificationException {
        InMemoryAggregateEventStream stream = streams.getOrDefault(aggregateDescriptor.toString(), new InMemoryAggregateEventStream());
        streams.put(aggregateDescriptor.toString(), stream.appendEvents(eventsWithMetaData, expectedVersion));
    }

    @Override
    public int countEventsByClassNames(List<String> eventClasses) {
        return streams.values()
            .stream()
            .map(stream -> stream.findEventsByClassNames(eventClasses))
            .reduce(new LinkedList<EventWithMetaData>(), (acc, element) -> {
                acc.addAll(element);
                return acc;
            }).size();
    }

    @Override
    public void loadEventsByClassNames(List<String> eventClasses, Predicate<EventWithMetaData> consumer) {
        List<EventWithMetaData> a = streams.values()
            .stream()
            .map(stream -> stream.findEventsByClassNames(eventClasses))
            .reduce(new LinkedList<EventWithMetaData>(), (acc, element) -> {
                acc.addAll(element);
                return acc;
            });
        for (EventWithMetaData eventWithMetaData : a) {
            if (!consumer.test(eventWithMetaData)) {
                return;
            }
        }
    }

    @Override
    public EventWithMetaData findEventById(String eventId) {
        return streams.values()
            .stream()
            .filter(stream -> stream.findEventById(eventId) != null)
            .findAny()
            .orElse(new InMemoryAggregateEventStream())
            .findEventById(eventId);
    }
}
