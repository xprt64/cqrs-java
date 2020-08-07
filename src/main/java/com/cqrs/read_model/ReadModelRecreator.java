package com.cqrs.read_model;

import com.cqrs.base.EventStore;
import com.cqrs.event_store.exceptions.StorageException;
import com.cqrs.read_model.progress.TaskProgressCalculator;
import com.cqrs.read_model.progress.TaskProgressReporter;

import java.util.List;
import java.util.function.Consumer;

public class ReadModelRecreator {
    final private EventStore eventStore;
    final private ReadModelEventApplier readModelEventApplier;
    final private ReadModelReflector readModelReflector;

    private TaskProgressReporter taskProgressReporter;
    private Consumer<String> logger;

    public ReadModelRecreator(
        EventStore eventStore,
        Consumer<String> logger,
        TaskProgressReporter taskProgressReporter,
        ReadModelEventApplier readModelEventApplier,
        ReadModelReflector readModelReflector
    ) {
        this.eventStore = eventStore;
        this.logger = logger;
        this.taskProgressReporter = taskProgressReporter;
        this.readModelEventApplier = readModelEventApplier;
        this.readModelReflector = readModelReflector;
    }

    public ReadModelRecreator(
        EventStore eventStore,
        ReadModelEventApplier readModelEventApplier,
        ReadModelReflector readModelReflector
    ) {
        this.eventStore = eventStore;
        this.readModelEventApplier = readModelEventApplier;
        this.readModelReflector = readModelReflector;
    }

    public void recreateRead(ReadModel readModel) throws StorageException {
        List<String> eventClasses = readModelReflector.getEventClassesFromReadModel(readModel.getClass().getCanonicalName());

        log("loading and applying events...");
        log(String.join(",", eventClasses));

        TaskProgressCalculator taskProgress = null != taskProgressReporter
            ? new TaskProgressCalculator(eventStore.countEventsByClassNames(eventClasses))
            : null;


        eventStore.loadEventsByClassNames(eventClasses, eventWithMetadata -> {
            readModelEventApplier.applyEventOnlyOnce(
                readModel,
                readModelReflector.getEventHandlerMethodNameForEvent(readModel.getClass().getCanonicalName(), eventWithMetadata.event.getClass().getCanonicalName()),
                eventWithMetadata
            );
            reportProgress(taskProgress);
            return true;
        });
    }

    private void reportProgress(TaskProgressCalculator taskProgress) {
        if (null != taskProgressReporter && null != taskProgress) {
            taskProgress.increment();
            taskProgressReporter
                .reportProgressUpdate(
                    taskProgress.getStep(),
                    taskProgress.getTotalSteps(),
                    taskProgress.calculateSpeed(),
                    taskProgress.calculateEta()
                );
        }
    }

    private void log(String message) {
        if (logger != null) {
            logger.accept(message);
        }
    }

    public ReadModelRecreator setTaskProgressReporter(TaskProgressReporter taskProgressReporter) {
        this.taskProgressReporter = taskProgressReporter;
        return this;
    }

    public ReadModelRecreator setLogger(Consumer<String> logger) {
        this.logger = logger;
        return this;
    }
}
