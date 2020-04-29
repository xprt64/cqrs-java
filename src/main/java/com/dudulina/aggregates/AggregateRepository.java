package com.dudulina.aggregates;

import com.dudulina.base.Aggregate;
import com.dudulina.events.EventWithMetaData;
import java.lang.reflect.InvocationTargetException;
import java.util.ConcurrentModificationException;
import java.util.List;

public interface AggregateRepository {

    Aggregate loadAggregate(AggregateDescriptor aggregateDescriptor)
        throws AggregateException, AggregateExecutionException;

    /**
     * @return List of events decorated with eventId and version
     */
    List<EventWithMetaData> saveAggregate(
        AggregateId aggregateId,
        Aggregate aggregate,
        List<EventWithMetaData> newEventsWithMeta
    ) throws ConcurrentModificationException;
}
