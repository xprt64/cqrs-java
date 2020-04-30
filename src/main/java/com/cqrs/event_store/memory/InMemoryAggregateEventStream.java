package com.cqrs.event_store.memory;

import com.cqrs.event_store.AggregateEventStream;
import com.cqrs.events.EventWithMetaData;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InMemoryAggregateEventStream implements AggregateEventStream {

    private ArrayList<EventWithMetaData> events = new ArrayList<>();
    private int version = 0;
    private int current = 0;

    public InMemoryAggregateEventStream() {
        this.events = new ArrayList<>();
    }

    public InMemoryAggregateEventStream(List<EventWithMetaData> events) {
        this.events = new ArrayList<>(events);
        version = events.size();
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public int count() {
        return events.size();
    }

    @Override
    public boolean hasNext() {
        return current < events.size() - 1;
    }

    @Override
    public EventWithMetaData next() {
        current++;
        return events.get(current - 1);
    }

    public InMemoryAggregateEventStream appendEvents(List<EventWithMetaData> newEvents) {
        events.addAll(newEvents);
        version = events.size();
        return this.cloneThis();
    }

    public InMemoryAggregateEventStream cloneThis() {
        return new InMemoryAggregateEventStream(events);
    }

    public EventWithMetaData findEventById(String eventId) {
        return events.stream()
                .filter(eventWithMetaData -> eventWithMetaData.metadata.eventId.equals(eventId))
                .findAny()
                .orElse(null);
    }

    public List<EventWithMetaData> findEventsByClassNames(List<String> eventClasses) {
        return events.stream()
                .filter(eventWithMetaData -> eventClasses.contains(eventWithMetaData.event.getClass().getCanonicalName()))
                .collect(Collectors.toList());
    }

}
