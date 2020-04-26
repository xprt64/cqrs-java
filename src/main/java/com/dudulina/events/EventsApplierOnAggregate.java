package com.dudulina.events;

import com.dudulina.aggregates.AggregateExecutionException;
import com.dudulina.base.Aggregate;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class EventsApplierOnAggregate {

    public void applyEventsOnAggregate(Aggregate aggregate, List<EventWithMetaData> prior) throws AggregateExecutionException
    {
        for (EventWithMetaData event : prior) {
            applyEvent(aggregate, event);
        }
    }

    private void applyEvent(Aggregate aggregate, EventWithMetaData eventWithMetaData) throws AggregateExecutionException
    {
        try {
            Method method = aggregate.getClass()
                .getMethod(Aggregate.METHOD_NAME, eventWithMetaData.event.getClass(),
                    eventWithMetaData.metadata.getClass());
            method.setAccessible(true);
            method.invoke(aggregate, eventWithMetaData.event, eventWithMetaData.metadata);
        } catch (NoSuchMethodException e) {
            try {
                Method method = aggregate.getClass()
                    .getMethod(Aggregate.METHOD_NAME, eventWithMetaData.event.getClass());
                method.setAccessible(true);
                method.invoke(aggregate, eventWithMetaData.event);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e1) {
                throw new AggregateExecutionException(aggregate, e);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new AggregateExecutionException(aggregate, e);
        }
    }
}
