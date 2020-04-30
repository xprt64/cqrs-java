package com.cqrs.event_store;

public interface EventSequence {
    public boolean isBefore(EventSequence other);
}
