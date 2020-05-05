package com.cqrs.commands;

import com.cqrs.annotations.CommandValidatorProcessor;
import com.cqrs.annotations.HandlersMapFromFile;

public class AnnotatedCommandValidatorsMap extends HandlersMapFromFile {

    public AnnotatedCommandValidatorsMap() {
        super(CommandValidatorProcessor.COMMAND_VALIDATORS_DIRECTORY);
    }
}
