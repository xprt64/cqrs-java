package com.cqrs.annotations;

import java.util.HashMap;
import java.util.List;

public interface HandlersMap {
    HashMap<String, List<Handler>> getMap(Class<?> anyClazzFromResourcePackage);

    class Handler{
        public final String handlerClass;
        public final String methodName;

        public Handler(String handlerClass, String methodName) {
            this.handlerClass = handlerClass;
            this.methodName = methodName;
        }
    }
}
