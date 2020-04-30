package com.cqrs.read_model;

import com.cqrs.base.EventStore;
import com.cqrs.event_store.SeekableEventStream;
import com.cqrs.read_model.progress.TaskProgressCalculator;
import com.cqrs.read_model.progress.TaskProgressReporter;
import com.cqrs.reflection.ReadModelReflector;
import java.util.List;
import java.util.function.Consumer;

public class ReadModelRecreator
{
    final private EventStore eventStore;
    final private Consumer<String> logger;
    final private TaskProgressReporter taskProgressReporter;
    final private ReadModelEventApplier readModelEventApplier;
    final private ReadModelReflector readModelReflector;

    public ReadModelRecreator(
        EventStore eventStore,
        Consumer<String> logger,
        TaskProgressReporter taskProgressReporter,
        ReadModelEventApplier readModelEventApplier,
        ReadModelReflector readModelReflector
    )
    {
        this.eventStore = eventStore;
        this.logger = logger;
        this.taskProgressReporter = taskProgressReporter;
        this.readModelEventApplier = readModelEventApplier;
        this.readModelReflector = readModelReflector;
    }

    public void recreateRead(ReadModel readModel)
    {
        List<String> eventClasses = readModelReflector.getEventClassesFromReadModel(readModel.getClass());

        log(String.join(",", eventClasses));
        log("loading events...");

        SeekableEventStream allEvents = eventStore.loadEventsByClassNames(eventClasses);

        log("applying events...");

        TaskProgressCalculator taskProgress =
            null != taskProgressReporter ? new TaskProgressCalculator(allEvents.count()) : null;

        allEvents.forEachRemaining(eventWithMetadata -> {
            readModelEventApplier.applyEventOnlyOnce(
                readModel,
                readModelReflector.getEventHandlerForEvent(eventWithMetadata.event.getClass()),
                eventWithMetadata
            );
            reportProgress(taskProgress);
        });
    }

    private void reportProgress(TaskProgressCalculator taskProgress)
    {
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

    private void log(String message)
    {
        if (logger != null) {
            logger.accept(message);
        }
    }
}
