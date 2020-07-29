package com.cqrs.events;

import com.cqrs.annotations.EventHandlersProcessor;
import com.cqrs.annotations.HandlersMapFromFile;
import com.cqrs.util.ResourceReader;

public class AnnotatedReadModelEventHandlersMap extends HandlersMapFromFile {

    public AnnotatedReadModelEventHandlersMap(ResourceReader resourceReader) {
        super(resourceReader, EventHandlersProcessor.EVENT_HANDLERS_DIRECTORY);
    }
}
