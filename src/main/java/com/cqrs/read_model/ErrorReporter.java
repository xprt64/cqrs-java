package com.cqrs.read_model;

import com.cqrs.events.EventWithMetaData;

public interface ErrorReporter {

    public void reportEventApplyError(ReadModel readModel, String methodName,
        EventWithMetaData eventWithMetadata, Throwable exception);
}
