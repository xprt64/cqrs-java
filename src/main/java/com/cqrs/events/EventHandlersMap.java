package com.cqrs.events;

import java.util.HashMap;
import java.util.List;

public interface EventHandlersMap {
    HashMap<String, List<Handler>> getMap();

    class Handler{
        public final String handlerClass;
        public final String methodName;

        public Handler(String handlerClass, String methodName) {
            this.handlerClass = handlerClass;
            this.methodName = methodName;
        }
    }
}
