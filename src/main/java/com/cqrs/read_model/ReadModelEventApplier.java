package com.cqrs.read_model;

import com.cqrs.events.EventWithMetaData;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReadModelEventApplier {

    final private OnlyOnceTracker onlyOnceTracker;
    final private ErrorReporter errorReporter;

    public ReadModelEventApplier(
        OnlyOnceTracker onlyOnceTracker,
        ErrorReporter errorReporter)
    {
        this.onlyOnceTracker = onlyOnceTracker;
        this.errorReporter = errorReporter;
    }

    public void applyEventOnlyOnce(
        ReadModel readModel,
        String methodName,
        EventWithMetaData eventWithMetadata)
    {
        if (onlyOnceTracker.isEventAlreadyApplied(readModel, eventWithMetadata.metadata.eventId)) {
            return;
        }
        onlyOnceTracker.markEventAsApplied(readModel, eventWithMetadata.metadata.eventId);
        applyEvent(readModel, methodName, eventWithMetadata);
    }


    private void applyEvent(
        ReadModel readModel,
        String methodName,
        EventWithMetaData eventWithMetadata)
    {
        try {
            Method method = readModel.getClass()
                .getDeclaredMethod(methodName, eventWithMetadata.event.getClass(),
                    eventWithMetadata.metadata.getClass());
            method.setAccessible(true);
            method.invoke(readModel, eventWithMetadata.event, eventWithMetadata.metadata);
        } catch (NoSuchMethodException e) {
            try {
                Method method = readModel.getClass()
                    .getDeclaredMethod(methodName, eventWithMetadata.event.getClass());
                method.setAccessible(true);
                method.invoke(readModel, eventWithMetadata.event);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException noSuchMethodException) {
                errorReporter.reportEventApplyError(
                    readModel,
                    methodName,
                    eventWithMetadata,
                    noSuchMethodException
                );
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            errorReporter.reportEventApplyError(
                readModel,
                methodName,
                eventWithMetadata,
                e
            );
        }
    }
}
