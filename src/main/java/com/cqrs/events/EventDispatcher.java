package com.cqrs.events;

public interface EventDispatcher {
    void dispatchEvent(EventWithMetaData eventWithMetadata);
}
