package com.cqrs.event_store.memory;

import com.cqrs.events.EventWithMetaData;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class InMemoryAggregateEventStream {

    private ArrayList<EventWithMetaData> events = new ArrayList<>();
    private int current = 0;

    public InMemoryAggregateEventStream() {
        this.events = new ArrayList<>();
    }

    public InMemoryAggregateEventStream(List<EventWithMetaData> events) {
        this.events = new ArrayList<>(events);
    }

    public int count() {
        return events.size();
    }

    public boolean hasNext() {
        return current < events.size();
    }

    public EventWithMetaData next() {
        return events.get(current++);
    }

    public InMemoryAggregateEventStream appendEvents(List<EventWithMetaData> newEvents, int expectedVersion) {
        List<EventWithMetaData> newEventsDecorated = new LinkedList<>();
        for(EventWithMetaData event:newEvents){
            newEventsDecorated.add(event.withVersion(++expectedVersion));
        }
        events.addAll(newEventsDecorated);
        return this;
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
