package com.dudulina.events;

public interface EventListenerFactory {
    Object factory(Class<?> clazz);
}
