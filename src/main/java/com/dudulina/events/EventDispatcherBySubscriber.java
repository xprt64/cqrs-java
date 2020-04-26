package com.dudulina.events;

import com.dudulina.base.Event;
import java.util.List;
import java.util.function.BiConsumer;

public class EventDispatcherBySubscriber {

    private final EventSubscriber eventSubscriber;
    private final ErrorReporter errorReporter;

    public EventDispatcherBySubscriber(
        EventSubscriber eventSubscriber,
        ErrorReporter errorReporter)
    {
        this.eventSubscriber = eventSubscriber;
        this.errorReporter = errorReporter;
    }

    public void dispatchEvent(EventWithMetaData eventWithMetadata)
    {
        List<BiConsumer<Event, MetaData>> listeners = eventSubscriber
            .getListenersForEvent(eventWithMetadata.event);

        listeners.forEach(listener -> {
            try {
                listener.accept(eventWithMetadata.event, eventWithMetadata.metadata);
            } catch (Throwable throwable) {
                this.errorReporter.reportEventDispatchError(
                    listener,
                    eventWithMetadata,
                    throwable
                );
            }
        });
    }
}


