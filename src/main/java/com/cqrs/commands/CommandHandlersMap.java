package com.cqrs.commands;

import java.util.HashMap;

public interface CommandHandlersMap {
    HashMap<String, Handler> getMap();

    class Handler{
        public final String handlerClass;
        public final String methodName;

        public Handler(String handlerClass, String methodName) {
            this.handlerClass = handlerClass;
            this.methodName = methodName;
        }
    }
}
