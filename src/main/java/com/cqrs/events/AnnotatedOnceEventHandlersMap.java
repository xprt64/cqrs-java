package com.cqrs.events;

import com.cqrs.annotations.HandlersMapFromFile;
import com.cqrs.annotations.OnceEventHandlersProcessor;
import com.cqrs.util.ResourceReader;

public class AnnotatedOnceEventHandlersMap extends HandlersMapFromFile {

    public AnnotatedOnceEventHandlersMap(ResourceReader resourceReader) {
        super(resourceReader,OnceEventHandlersProcessor.EVENT_HANDLERS_DIRECTORY);
    }
}
