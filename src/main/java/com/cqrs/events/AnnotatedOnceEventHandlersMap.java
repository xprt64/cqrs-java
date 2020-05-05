package com.cqrs.events;

import com.cqrs.annotations.HandlersMapFromFile;
import com.cqrs.annotations.OnceEventHandlersProcessor;

public class AnnotatedOnceEventHandlersMap extends HandlersMapFromFile {

    public AnnotatedOnceEventHandlersMap() {
        super(OnceEventHandlersProcessor.EVENT_HANDLERS_DIRECTORY);
    }
}
