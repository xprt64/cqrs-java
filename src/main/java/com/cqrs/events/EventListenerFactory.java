package com.cqrs.events;

public interface EventListenerFactory {
    Object factory(Class<?> clazz);
}
