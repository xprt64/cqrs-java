package com.cqrs.events;

import com.cqrs.annotations.OnceEventHandlersProcessor;

public class AnnotatedOnceEventHandlersMap extends EventHandlersMapFromFile {

    public AnnotatedOnceEventHandlersMap() {
        super(OnceEventHandlersProcessor.EVENT_HANDLERS_DIRECTORY);
    }
}
