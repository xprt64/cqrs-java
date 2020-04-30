package com.cqrs.commands;

import com.cqrs.base.Command;

public class MetadataWrapper {
    public CommandWithMetadata wrapCommandWithMetadata(Command $command, CommandMetaData metadata){
        return new CommandWithMetadata($command, metadata);
    }
}
