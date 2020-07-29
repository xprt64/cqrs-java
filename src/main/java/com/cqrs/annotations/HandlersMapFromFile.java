package com.cqrs.annotations;

import com.cqrs.util.ResourceReader;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

abstract public class HandlersMapFromFile implements HandlersMap {

    private final String DIRECTORY_PATH;

    private ResourceReader resourceReader;
    private HashMap<String, List<MessageHandler>> cache;

    public HandlersMapFromFile(ResourceReader resourceReader, String DIRECTORY_PATH) {
        this.resourceReader = resourceReader;
        this.DIRECTORY_PATH = DIRECTORY_PATH;
    }

    @Override
    public HashMap<String, List<MessageHandler>> getMap() {
        if(null == cache){
            cache = getMapNonCached();
        }
        return cache;
    }

    private HashMap<String, List<MessageHandler>> getMapNonCached() {
        HashMap<String, List<MessageHandler>> handlersPerMessage = new HashMap<>();
        resourceReader.forEachLineInDirectory(
            DIRECTORY_PATH,
            (aggregateName, line) -> {
                String[] commandAndMethod = line.split(",", 2);
                final String command = commandAndMethod[0];
                final String method = commandAndMethod[1];
                List<MessageHandler> existing = handlersPerMessage.getOrDefault(command, new LinkedList<>());
                existing.add(new MessageHandler(aggregateName, method));
                handlersPerMessage.put(command, existing);
            }
        );
        return handlersPerMessage;
    }

}
