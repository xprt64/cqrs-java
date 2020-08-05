package com.cqrs.read_model.readmodel_reflector;

import com.cqrs.read_model.ReadModelReflector;

import java.util.HashMap;
import java.util.List;

public class CachedReadModelsReflector implements ReadModelReflector {
    private final ReadModelReflector decorated;
    private final HashMap<String, List<String>> eventClassesCache = new HashMap<>();
    private final HashMap<String, String> handlerCache = new HashMap<>();

    public CachedReadModelsReflector(ReadModelReflector decorated) {
        this.decorated = decorated;
    }

    @Override
    public List<String> getEventClassesFromReadModel(String readModelClass) {
        if (null == eventClassesCache.get(readModelClass)) {
            eventClassesCache.put(readModelClass, decorated.getEventClassesFromReadModel(readModelClass));
        }
        return eventClassesCache.get(readModelClass);
    }

    @Override
    public String getEventHandlerMethodNameForEvent(String readModelClass, String eventClass) {
        if (null == handlerCache.get(readModelClass)) {
            handlerCache.put(readModelClass, decorated.getEventHandlerMethodNameForEvent(readModelClass, eventClass));
        }
        return handlerCache.get(readModelClass);
    }
}
