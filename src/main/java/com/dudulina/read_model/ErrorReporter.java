package com.dudulina.read_model;

import com.dudulina.events.EventWithMetaData;

public interface ErrorReporter {

    public void reportEventApplyError(ReadModel readModel, String methodName,
        EventWithMetaData eventWithMetadata, Throwable exception);
}
