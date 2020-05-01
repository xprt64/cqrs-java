package com.cqrs.aggregates;

import com.cqrs.base.Aggregate;
import com.cqrs.base.Event;
import com.cqrs.events.MetaData;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

public class EventApplierOnAggregate
{
    public static final String version = "v1";

    public static void applyEvent(Aggregate aggregate, Event event, MetaData metaData)
    {
        Objects.requireNonNull(aggregate);
        Objects.requireNonNull(event);
        try {
            try {
                Method method = aggregate.getClass().getDeclaredMethod(Aggregate.METHOD_NAME, event.getClass(), MetaData.class);
                method.setAccessible(true);
                method.invoke(aggregate, event, metaData);
            } catch (NoSuchMethodException e2) {
                try{
                    Method method = aggregate.getClass().getDeclaredMethod(Aggregate.METHOD_NAME, event.getClass());
                    method.setAccessible(true);
                    method.invoke(aggregate, event);
                }
                catch (NoSuchMethodException e1){
                    System.out.println(version + "/Aggregate is not interested in event " + event.getClass().getCanonicalName());
                    //do nothing, Aggregate is not interested in own event
                }
             }
        } catch (InvocationTargetException | IllegalAccessException e) {
            //what do we do when there is a problem with the call?
            //@todo
        }
    }
}
