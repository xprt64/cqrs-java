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

    public EventSubscriberByMap(EventHandlersMap map) {
        this.map = map;
    }

    @Override
    public List<BiConsumer<Event, MetaData>> getListenersForEvent(Event event) {
        return Stream.of(map.getMap().getOrDefault(event.getClass().getCanonicalName(), new String[][]{}))
            .map(listenerDescriptor -> (BiConsumer<Event, MetaData>) (event1, metaData) -> {
                String listenerClass = listenerDescriptor[0];
                String listenerMethod = listenerDescriptor[1];
                try{
                    try {
                        Class<?> clazz = Class.forName(listenerClass);
                        Object listener = factoryObject(clazz);
                        try {
                            Method method = clazz.getDeclaredMethod(listenerMethod, Event.class);
                            method.setAccessible(true);
                            method.invoke(listener, event);
                        } catch (NoSuchMethodException e) {
                            Method method = clazz.getDeclaredMethod(listenerMethod, Event.class, MetaData.class);
                            method.setAccessible(true);
                            method.invoke(listener, event, metaData);
                        }
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
                catch (Exception e){
                    e.printStackTrace();

                }
            })
            .collect(Collectors.toList());
    }

    private Object factoryObject(Class<?> clazz) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return clazz.getDeclaredConstructor().newInstance();
    }
}
