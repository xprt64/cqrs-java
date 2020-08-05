package com.cqrs.read_model.readmodel_reflector;

import com.cqrs.annotations.EventHandlersProcessor;
import com.cqrs.util.ResourceReader;

public class AnnotatedReadModelsReflector extends FromHandlersFileReadModelsReflector {
    public AnnotatedReadModelsReflector(ResourceReader resourceReader) {

        super(resourceReader, EventHandlersProcessor.EVENT_HANDLERS_DIRECTORY);
    }

}
