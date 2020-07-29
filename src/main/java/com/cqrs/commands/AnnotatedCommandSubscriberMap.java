package com.cqrs.commands;

import com.cqrs.annotations.CommandHandlersProcessor;
import com.cqrs.annotations.CommandValidatorProcessor;
import com.cqrs.annotations.HandlersMapFromFile;
import com.cqrs.util.ResourceReader;

public class AnnotatedCommandSubscriberMap extends HandlersMapFromFile {

    public AnnotatedCommandSubscriberMap(ResourceReader resourceReader) {
        super(resourceReader, CommandHandlersProcessor.AGGREGATE_COMMAND_HANDLERS_DIRECTORY);
    }

}
