package com.cqrs.aggregates;

import com.cqrs.base.Aggregate;
import com.cqrs.base.Event;
import com.cqrs.events.MetaData;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

public class EventApplierOnAggregate {
    public static void applyEvent(Aggregate aggregate, Event event, MetaData metaData) {
        Objects.requireNonNull(aggregate);
        Objects.requireNonNull(event);
        try {
            try {
                Method method = aggregate.getClass().getDeclaredMethod(Aggregate.METHOD_NAME, event.getClass(), MetaData.class);
                method.setAccessible(true);
                method.invoke(aggregate, event, metaData);
            } catch (NoSuchMethodException e2) {
                try {
                    Method method = aggregate.getClass().getDeclaredMethod(Aggregate.METHOD_NAME, event.getClass());
                    method.setAccessible(true);
                    method.invoke(aggregate, event);
                } catch (NoSuchMethodException e1) {
                    System.out.println("Warning: Aggregate does not apply its own event of type " + event.getClass().getCanonicalName());
                    //do nothing, Aggregate is not interested in its own event
                }
            }
        } catch (InvocationTargetException e) {
            throw new AggregateEventApplyException(aggregate, event, metaData.eventId, e.getCause());
        } catch (IllegalAccessException e) {
            throw new AggregateEventApplyException(aggregate, event, metaData.eventId, e);
        }
    }
}
