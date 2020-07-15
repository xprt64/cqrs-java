package com.cqrs.questions;

public interface ErrorReporter {

    void reportQuestionDispatchError(
        Object listenerInstance,
        String listenerClass,
        String methodName,
        Object question,
        Throwable throwable
    );
}
