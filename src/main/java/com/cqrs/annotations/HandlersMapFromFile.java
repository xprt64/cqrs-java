package com.cqrs.annotations;

import com.cqrs.annotations.HandlersMap;
import com.cqrs.util.ResourceReader;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

abstract public class HandlersMapFromFile implements HandlersMap {

    private final String DIRECTORY_PATH;

    private ResourceReader resourceReader = new ResourceReader();

    public HandlersMapFromFile(String DIRECTORY_PATH) {
        this.DIRECTORY_PATH = DIRECTORY_PATH;
    }

    @Override
    public HashMap<String, List<Handler>> getMap(Class<?> anyClazzFromResourcePackage) {
        HashMap<String, List<Handler>> handlersPerMessage = new HashMap<>();
        resourceReader.forEachLineInDirectory(
            anyClazzFromResourcePackage,
            DIRECTORY_PATH,
            (aggregateName, line) -> {
                String[] commandAndMethod = line.split(",", 2);
                final String command = commandAndMethod[0];
                final String method = commandAndMethod[1];
                List<Handler> existing = handlersPerMessage.getOrDefault(command, new LinkedList<>());
                existing.add(new Handler(aggregateName, method));
                handlersPerMessage.put(command, existing);
            }
        );
        return handlersPerMessage;
    }

    public void setResourceReader(ResourceReader resourceReader) {
        this.resourceReader = resourceReader;
    }
}
