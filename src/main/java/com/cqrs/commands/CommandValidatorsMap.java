package com.cqrs.commands;

import java.util.HashMap;
import java.util.List;

public interface CommandValidatorsMap {
    HashMap<String, List<Handler>> getMap(Class<?> clazz);

    class Handler{
        public final String handlerClass;
        public final String methodName;

        public Handler(String handlerClass, String methodName) {
            this.handlerClass = handlerClass;
            this.methodName = methodName;
        }
    }
}
