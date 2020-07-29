package com.cqrs.questions.exceptions;

import com.cqrs.annotations.MessageHandler;

public class HandlerException extends RuntimeException {
    public final MessageHandler handler;

    public HandlerException(String message, MessageHandler handler, Throwable cause) {
        super(message, cause);
        this.handler = handler;
    }

    public HandlerException(String message) {
        super(message);
        this.handler = null;
    }
}
