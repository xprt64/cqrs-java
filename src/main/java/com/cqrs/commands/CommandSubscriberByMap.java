package com.cqrs.commands;

import com.cqrs.annotations.CommandHandlersProcessor;
import com.cqrs.util.ResourceReader;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;

public class CommandSubscriberByMap implements CommandSubscriber {

    private final String DIRECTORY_PATH;

    private ResourceReader resourceReader = new ResourceReader();

    public CommandSubscriberByMap() {
        DIRECTORY_PATH = CommandHandlersProcessor.AGGREGATE_COMMAND_HANDLERS_DIRECTORY;
    }

    public CommandSubscriberByMap(String DIRECTORY_PATH) {
        this.DIRECTORY_PATH = DIRECTORY_PATH;
    }

    @Override
    public CommandHandlerDescriptor getAggregateForCommand(Class<?> commandClass) throws CommandHandlerNotFound {
        CommandHandlerDescriptor entry = getMap().get(commandClass.getCanonicalName());
        if (entry == null) {
            throw new CommandHandlerNotFound(commandClass.getCanonicalName());
        }
        return entry;
    }

    private HashMap<String, CommandHandlerDescriptor> getMap() {
        HashMap<String, CommandHandlerDescriptor> handlerPerCommand = new HashMap<>();
        resourceReader.forEachLineInDirectory(DIRECTORY_PATH, (aggregateName, line) -> {
            //System.out.println(line);
            String[] commandAndMethod = line.split(",", 2);
            final String command = commandAndMethod[0];
            final String method = commandAndMethod[1];
            handlerPerCommand.put(command, new CommandHandlerDescriptor(aggregateName, method));
        });
        return handlerPerCommand;
    }

    public void setResourceReader(ResourceReader resourceReader) {
        this.resourceReader = resourceReader;
    }
}
