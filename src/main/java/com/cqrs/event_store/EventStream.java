package com.cqrs.event_store;

import com.cqrs.events.EventWithMetaData;
import java.util.Iterator;

public interface EventStream extends Iterator<EventWithMetaData> {
    public int count();
}
