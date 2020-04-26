package com.dudulina.event_store;

import com.dudulina.events.EventWithMetaData;
import java.util.Iterator;

public interface EventStream extends Iterator<EventWithMetaData> {
    public int count();
}
