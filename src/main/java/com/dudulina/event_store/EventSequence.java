package com.dudulina.event_store;

public interface EventSequence {
    public boolean isBefore(EventSequence other);
}
