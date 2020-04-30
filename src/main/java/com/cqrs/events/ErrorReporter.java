package com.cqrs.events;

public interface ErrorReporter {

    void reportEventDispatchError(
        Object listenerInstance,
        String listenerClass,
        String methodName,
        EventWithMetaData eventWithMetaData,
        Throwable throwable
    );
}
