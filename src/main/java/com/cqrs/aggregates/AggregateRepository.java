package com.cqrs.aggregates;

import com.cqrs.base.Aggregate;
import com.cqrs.events.EventWithMetaData;

import java.util.ConcurrentModificationException;
import java.util.List;

public interface AggregateRepository {

    Aggregate loadAggregate(AggregateDescriptor aggregateDescriptor)
        throws AggregateException, AggregateExecutionException;

    /**
     * @return List of events decorated with eventId and version
     */
    List<EventWithMetaData> saveAggregate(
        String aggregateId,
        Aggregate aggregate,
        List<EventWithMetaData> newEventsWithMeta
    ) throws ConcurrentModificationException;
}
