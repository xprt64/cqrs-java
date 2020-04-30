package com.cqrs.events;

public class CompositeEventDispatcher implements EventDispatcher {

    private final EventDispatcher[] eventDispatchers;

    public CompositeEventDispatcher(EventDispatcher... eventDispatchers)
    {
        this.eventDispatchers = eventDispatchers;
    }

    @Override
    public void dispatchEvent(EventWithMetaData eventWithMetadata)
    {
        for (EventDispatcher eventDispatcher : eventDispatchers) {
            eventDispatcher.dispatchEvent(eventWithMetadata);
        }
    }
}
