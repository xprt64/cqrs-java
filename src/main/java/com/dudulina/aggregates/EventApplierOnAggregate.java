package com.dudulina.aggregates;

import com.dudulina.base.Aggregate;
import com.dudulina.base.Event;
import com.dudulina.events.MetaData;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

public class EventApplierOnAggregate
{
    public static void applyEvent(Aggregate aggregate, Event event, MetaData metaData)
    {
        Objects.requireNonNull(aggregate);
        Objects.requireNonNull(event);
        try {
            try {
                Method method = aggregate.getClass().getMethod(Aggregate.METHOD_NAME, event.getClass(), MetaData.class);
                method.setAccessible(true);
                method.invoke(aggregate, event, metaData);
            } catch (NoSuchMethodException e) {
                Method method = aggregate.getClass().getMethod(Aggregate.METHOD_NAME, event.getClass());
                method.setAccessible(true);
                method.invoke(aggregate, event);
            }
        } catch (NoSuchMethodException e) {
            //do nothing, Aggregate is not interested in own event
        } catch (InvocationTargetException | IllegalAccessException e) {
            //what do we do when there is a problem with the call?
            //@todo
        }
    }
}
