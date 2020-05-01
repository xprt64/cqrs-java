package com.cqrs.events;

import com.cqrs.annotations.EventHandlersProcessor;

public class AnnotatedReadModelEventHandlersMap extends EventHandlersMapFromFile {

    public AnnotatedReadModelEventHandlersMap() {
        super(EventHandlersProcessor.EVENT_HANDLERS_DIRECTORY);
    }
}
