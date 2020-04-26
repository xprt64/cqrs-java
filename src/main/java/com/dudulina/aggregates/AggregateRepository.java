package com.dudulina.aggregates;

import com.dudulina.base.Aggregate;
import com.dudulina.events.EventWithMetaData;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public interface AggregateRepository {

    public Aggregate loadAggregate(AggregateDescriptor aggregateDescriptor)
        throws AggregateException, AggregateExecutionException;

    /**
     * @return List of events decorated with eventId and version
     */
    public List<EventWithMetaData> saveAggregate(
        AggregateId aggregateId,
        Aggregate aggregate,
        List<EventWithMetaData> newEventsWithMeta
    );
}
