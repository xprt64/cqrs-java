package com.dudulina.events;

public interface EventDispatcher {
    void dispatchEvent(EventWithMetaData eventWithMetadata);
}
