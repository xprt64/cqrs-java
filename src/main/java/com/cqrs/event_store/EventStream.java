package com.cqrs.event_store;

import com.cqrs.event_store.exceptions.StorageException;
import com.cqrs.events.EventWithMetaData;
import java.util.Iterator;

public interface EventStream extends Iterator<EventWithMetaData> {
    int count() throws StorageException;
}
