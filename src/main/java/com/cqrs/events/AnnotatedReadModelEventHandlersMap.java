package com.cqrs.events;

import com.cqrs.annotations.EventHandlersProcessor;
import com.cqrs.annotations.HandlersMapFromFile;

public class AnnotatedReadModelEventHandlersMap extends HandlersMapFromFile {

    public AnnotatedReadModelEventHandlersMap() {
        super(EventHandlersProcessor.EVENT_HANDLERS_DIRECTORY);
    }
}
