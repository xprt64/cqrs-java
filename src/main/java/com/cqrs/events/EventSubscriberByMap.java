package com.cqrs.events;

import com.cqrs.annotations.HandlersMap;
import com.cqrs.base.Event;
import com.cqrs.annotations.HandlersMap.Handler;
import com.cqrs.infrastructure.AbstractFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class EventSubscriberByMap implements EventSubscriber {

    private final HandlersMap map;
    private final AbstractFactory factory;
    private final ErrorReporter errorReporter;

    public EventSubscriberByMap(
        HandlersMap map,
        AbstractFactory factory,
        ErrorReporter errorReporter
    ) {
        this.map = map;
        this.factory = factory;
        this.errorReporter = errorReporter;
    }

    @Override
    public List<BiConsumer<Event, MetaData>> getListenersForEvent(Event event) {
        final List<Handler> handlersForThisEvent = map.getMap(event.getClass()).getOrDefault(event.getClass().getCanonicalName(), new LinkedList<>());
        return handlersForThisEvent.stream()
            .map(listenerDescriptor -> (BiConsumer<Event, MetaData>) (event1, metaData) -> {
                Object listener = null;
                try {
                    Class<?> clazz = Class.forName(listenerDescriptor.handlerClass);
                    listener = factoryObject(clazz);
                    try {
                        Method method = clazz.getDeclaredMethod(listenerDescriptor.methodName, event.getClass());
                        method.setAccessible(true);
                        method.invoke(listener, event);
                    } catch (NoSuchMethodException e) {
                        Method method = clazz.getDeclaredMethod(listenerDescriptor.methodName, event.getClass(), metaData.getClass());
                        method.setAccessible(true);
                        method.invoke(listener, event, metaData);
                    }
                }
                catch (InvocationTargetException e){
                    errorReporter.reportEventDispatchError(
                        listener,
                        listenerDescriptor.handlerClass,
                        listenerDescriptor.methodName,
                        new EventWithMetaData(event1, metaData),
                        e.getCause()
                    );
                }
                catch (Throwable e) {
                    errorReporter.reportEventDispatchError(
                        listener,
                        listenerDescriptor.handlerClass,
                        listenerDescriptor.methodName,
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
