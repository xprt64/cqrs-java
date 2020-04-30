package com.cqrs.event_store;

public interface AggregateEventStream extends EventStream {

    public int getVersion();
}
