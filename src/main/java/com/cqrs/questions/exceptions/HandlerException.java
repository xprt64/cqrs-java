package com.cqrs.questions.exceptions;

import com.cqrs.annotations.HandlersMap.Handler;

public class HandlerException extends RuntimeException {
    public final Handler handler;

    public HandlerException(String message, Handler handler, Throwable cause) {
        super(message, cause);
        this.handler = handler;
    }

    public HandlerException(String message) {
        super(message);
        this.handler = null;
    }
}
