package com.dudulina.command;

import com.dudulina.base.Command;

public class MetadataWrapper {
    public CommandWithMetadata wrapCommandWithMetadata(Command $command, CommandMetaData metadata){
        return new CommandWithMetadata($command, metadata);
    }
}
