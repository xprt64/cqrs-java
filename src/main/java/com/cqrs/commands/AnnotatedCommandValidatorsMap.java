package com.cqrs.commands;

import com.cqrs.annotations.CommandValidatorProcessor;
import com.cqrs.annotations.HandlersMapFromFile;
import com.cqrs.util.ResourceReader;

public class AnnotatedCommandValidatorsMap extends HandlersMapFromFile {

    public AnnotatedCommandValidatorsMap(ResourceReader resourceReader) {
        super(resourceReader, CommandValidatorProcessor.COMMAND_VALIDATORS_DIRECTORY);
    }
}
