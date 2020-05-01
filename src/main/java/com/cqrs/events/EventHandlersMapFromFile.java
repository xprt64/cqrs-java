package com.cqrs.events;

import com.cqrs.util.ResourceReader;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

abstract public class EventHandlersMapFromFile implements EventHandlersMap {

    private final String DIRECTORY_PATH;

    private ResourceReader resourceReader = new ResourceReader();

    public EventHandlersMapFromFile(String DIRECTORY_PATH) {
        this.DIRECTORY_PATH = DIRECTORY_PATH;
    }

    @Override
    public HashMap<String, List<Handler>> getMap() {
        HashMap<String, List<Handler>> handlersPerCommand = new HashMap<>();
        resourceReader.forEachLineInDirectory(DIRECTORY_PATH, (aggregateName, line) -> {
            String[] commandAndMethod = line.split(",", 2);
            final String command = commandAndMethod[0];
            final String method = commandAndMethod[1];
            List<Handler> existing = handlersPerCommand.getOrDefault(command, new LinkedList<>());
            existing.add(new Handler(aggregateName, method));
            handlersPerCommand.put(command, existing);
        });
        return handlersPerCommand;
    }

    public void setResourceReader(ResourceReader resourceReader) {
        this.resourceReader = resourceReader;
    }
}
