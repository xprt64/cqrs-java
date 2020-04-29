package com.dudulina.event_store.memory;

import com.dudulina.aggregates.AggregateDescriptor;
import com.dudulina.base.EventStore;
import com.dudulina.event_store.AggregateEventStream;
import com.dudulina.event_store.SeekableEventStream;
import com.dudulina.events.EventWithMetaData;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class InMemoryEventStore implements EventStore {

    HashMap<String, InMemoryAggregateEventStream> streams = new HashMap<>();

    @Override
    public AggregateEventStream loadEventsForAggregate(AggregateDescriptor aggregateDescriptor) {
        return streams.getOrDefault(aggregateDescriptor.toString(), new InMemoryAggregateEventStream());
    }

    @Override
    public void appendEventsForAggregate(
            AggregateDescriptor aggregateDescriptor,
            List<EventWithMetaData> eventsWithMetaData,
            int expectedVersion
    ) throws ConcurrentModificationException {
        InMemoryAggregateEventStream stream = (InMemoryAggregateEventStream) loadEventsForAggregate(aggregateDescriptor);
        if(stream.getVersion() != expectedVersion){
            throw new ConcurrentModificationException();
        }
        streams.put(aggregateDescriptor.toString(), stream.appendEvents(eventsWithMetaData));
    }

    @Override
    public SeekableEventStream loadEventsByClassNames(List<String> eventClasses) {
        return new InMemorySeekableEventStream(
                streams.values()
                        .stream()
                        .map(stream -> stream.findEventsByClassNames(eventClasses))
                        .reduce(new LinkedList<>(), (acc, element) -> {
                            acc.addAll(element);
                            return acc;
                        })
        );
    }

    @Override
    public int getAggregateVersion(AggregateDescriptor aggregateDescriptor) {
        return loadEventsForAggregate(aggregateDescriptor).getVersion();
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
