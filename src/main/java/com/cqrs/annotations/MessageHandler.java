package com.cqrs.annotations;

public class MessageHandler {
    public final String handlerClass;
    public final String methodName;
    public final int order;

    public MessageHandler(String handlerClass, String methodName) {
        this.handlerClass = handlerClass;
        this.methodName = methodName;
        this.order = 0;
    }

    public MessageHandler(String handlerClass, String methodName, int order) {
        this.handlerClass = handlerClass;
        this.methodName = methodName;
        this.order = order;
    }
}
