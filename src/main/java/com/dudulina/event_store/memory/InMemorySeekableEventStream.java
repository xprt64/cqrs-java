package com.dudulina.event_store.memory;

import com.dudulina.event_store.EventSequence;
import com.dudulina.event_store.SeekableEventStream;
import com.dudulina.events.EventWithMetaData;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class InMemorySeekableEventStream implements SeekableEventStream {

    final private List<EventWithMetaData> events;
    private List<EventWithMetaData> selectedEvents;
    private Iterator<EventWithMetaData> iterator;

    public InMemorySeekableEventStream(List<EventWithMetaData> events) {
        this.events = this.selectedEvents = new ArrayList<>(events);
        this.iterator = this.selectedEvents.iterator();
    }

    @Override
    public void afterSequence(EventSequence after) {
        this.selectedEvents = events.stream().filter(eventWithMetaData -> !eventWithMetaData.metadata.sequence.isBefore(after)).collect(Collectors.toList());
        this.iterator = this.selectedEvents.iterator();
    }

    @Override
    public void beforeSequence(EventSequence before) {
        this.selectedEvents = events.stream().filter(eventWithMetaData -> eventWithMetaData.metadata.sequence.isBefore(before)).collect(Collectors.toList());
        this.iterator = this.selectedEvents.iterator();
    }

    @Override
    public void sort(boolean chronological) {
        if (chronological) {
            this.events.sort((o1, o2) -> o1.metadata.dateCreated.compareTo(o2.metadata.dateCreated));
        }
    }

    @Override
    public int count() {
        return selectedEvents.size();
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public EventWithMetaData next() {
        return iterator.next();
    }
}
