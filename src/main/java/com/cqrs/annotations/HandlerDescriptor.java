package com.cqrs.annotations;

public class HandlerDescriptor {
    public final String parameterClass;
    public final String methodName;

    public HandlerDescriptor(String parameterClass, String methodName) {
        this.parameterClass = parameterClass;
        this.methodName = methodName;
    }
}
