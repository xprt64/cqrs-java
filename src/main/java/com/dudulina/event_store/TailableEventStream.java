package com.dudulina.event_store;

import com.dudulina.events.EventWithMetaData;
import java.util.List;
import java.util.function.Consumer;

public interface TailableEventStream {

    public void tail(
        Consumer<EventWithMetaData> callback,
        List<String> eventClasses,
        EventSequence afterSequence);
}
