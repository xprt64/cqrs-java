package com.dudulina.events;

import com.dudulina.base.Event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EventSubscriberByMap implements EventSubscriber {

    private final EventHandlersMap map;
    private final EventListenerFactory factory;
    private final ErrorReporter errorReporter;

    public EventSubscriberByMap(
        EventHandlersMap map,
        EventListenerFactory factory,
        ErrorReporter errorReporter
    ) {
        this.map = map;
        this.factory = factory;
        this.errorReporter = errorReporter;
    }

    @Override
    public List<BiConsumer<Event, MetaData>> getListenersForEvent(Event event) {
        return Stream.of(map.getMap().getOrDefault(event.getClass().getCanonicalName(), new String[][]{}))
            .map(listenerDescriptor -> (BiConsumer<Event, MetaData>) (event1, metaData) -> {
                String listenerClass = listenerDescriptor[0];
                String listenerMethod = listenerDescriptor[1];
                Object listener = null;
                try {
                    Class<?> clazz = Class.forName(listenerClass);
                    listener = factoryObject(clazz);
                    try {
                        Method method = clazz.getDeclaredMethod(listenerMethod, event.getClass());
                        method.setAccessible(true);
                        method.invoke(listener, event);
                    } catch (NoSuchMethodException e) {
                        Method method = clazz.getDeclaredMethod(listenerMethod, event.getClass(), metaData.getClass());
                        method.setAccessible(true);
                        method.invoke(listener, event, metaData);
                    }
                }
                catch (InvocationTargetException e){
                    errorReporter.reportEventDispatchError(
                        listener,
                        listenerClass,
                        listenerMethod,
                        new EventWithMetaData(event1, metaData),
                        e.getCause()
                    );
                }
                catch (Throwable e) {
                    errorReporter.reportEventDispatchError(
                        listener,
                        listenerClass,
                        listenerMethod,
                        new EventWithMetaData(event1, metaData),
                        e
                    );
                }
            })
            .collect(Collectors.toList());
    }

    private Object factoryObject(Class<?> clazz) {
        return factory.factory(clazz);
    }
}
