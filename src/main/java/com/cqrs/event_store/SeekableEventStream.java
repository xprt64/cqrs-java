package com.cqrs.event_store;

public interface SeekableEventStream extends EventStream {

    public void afterSequence(EventSequence after);

    public void beforeSequence(EventSequence before);

    public void sort(boolean chronological);
}
