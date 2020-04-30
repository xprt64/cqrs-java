package com.cqrs.event_store;

import com.cqrs.events.EventWithMetaData;
import java.util.List;
import java.util.function.Consumer;

public interface TailableEventStream {

    public void tail(
        Consumer<EventWithMetaData> callback,
        List<String> eventClasses,
        EventSequence afterSequence);
}
