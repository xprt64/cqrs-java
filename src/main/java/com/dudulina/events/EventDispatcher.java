package com.dudulina.events;

public interface EventDispatcher {
    public void dispatchEvent(EventWithMetaData eventWithMetadata);
}
