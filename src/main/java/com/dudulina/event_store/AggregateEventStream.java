package com.dudulina.event_store;

public interface AggregateEventStream extends EventStream {

    public int getVersion();
}
