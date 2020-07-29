package com.cqrs.annotations;

public class MessageHandler {
    public final String handlerClass;
    public final String methodName;

    public MessageHandler(String handlerClass, String methodName) {
        this.handlerClass = handlerClass;
        this.methodName = methodName;
    }
}
