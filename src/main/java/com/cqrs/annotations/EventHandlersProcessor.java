package com.cqrs.annotations;

import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;

@SupportedAnnotationTypes("com.cqrs.annotations.EventHandler")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class EventHandlersProcessor extends AbstractEventHandlerProcessor {

    public static final String EVENT_HANDLERS_DIRECTORY = "com_cqrs_annotations_EventHandlers";

    @Override
    protected String getOutputDirectory() {
        return EVENT_HANDLERS_DIRECTORY;
    }
}

