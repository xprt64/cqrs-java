package com.cqrs.events;

import com.cqrs.base.Event;

import java.util.List;
import java.util.function.BiConsumer;

public class EventDispatcherBySubscriber implements EventDispatcher {

    private final EventSubscriber eventSubscriber;

    public EventDispatcherBySubscriber(EventSubscriber eventSubscriber) {
        this.eventSubscriber = eventSubscriber;
    }

    public void dispatchEvent(EventWithMetaData eventWithMetadata) {
        List<BiConsumer<Event, MetaData>> listeners =
            eventSubscriber.getListenersForEvent(eventWithMetadata.event);

        listeners.forEach(listener -> {
            listener.accept(eventWithMetadata.event, eventWithMetadata.metadata);
        });
    }
}


