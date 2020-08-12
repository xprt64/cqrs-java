package com.cqrs.annotations;

import com.cqrs.util.ResourceReader;

import java.util.Comparator;
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
            (listenerName, line) -> {
                String[] messageClassAndAndMethod = line.split(",", 3);
                final String messageClass = messageClassAndAndMethod[0];
                final String methodName = messageClassAndAndMethod[1];
                final int order = (messageClassAndAndMethod.length > 2) ? Integer.parseUnsignedInt(messageClassAndAndMethod[2]) : 0;

                List<MessageHandler> existing = handlersPerMessage.getOrDefault(messageClass, new LinkedList<>());
                existing.add(new MessageHandler(listenerName, methodName, order));
                handlersPerMessage.put(messageClass, existing);
            }
        );
        for(String messageClass:handlersPerMessage.keySet()){
            List<MessageHandler> handlers = handlersPerMessage.get(messageClass);
            handlers.sort(Comparator.comparingInt(o -> o.order));
        }
        return handlersPerMessage;
    }
}
