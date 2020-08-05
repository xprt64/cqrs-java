package com.cqrs.read_model;

import com.cqrs.base.Event;
import com.cqrs.events.EventWithMetaData;
import com.cqrs.events.MetaData;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class ReadModelEventApplierFailingTest {

    @Test
    void applyEventOnlyOnce() {
        AtomicReference<ArrayList<Throwable>> actualExceptions = new AtomicReference<>(new ArrayList<>());

        ReadModelEventApplier sut = new ReadModelEventApplier(
            new OnlyOnceTracker(),
            (readModel, methodName, eventWithMetadata, exception) -> actualExceptions.get().add(exception)
        );
        RM readModel = new RM();
        sut.applyEventOnlyOnce(readModel, "on", new EventWithMetaData(new Event1(), new MetaData(null, "agg1", "aggC", null, 1, null, "id1")));
        sut.applyEventOnlyOnce(readModel, "on", new EventWithMetaData(new Event2(), new MetaData(null, "agg1", "aggC", null, 1, null, "id2")));

        assertEquals(2, actualExceptions.get().size());
    }

    public static class RM implements ReadModel {

        @Override
        public void clearModel() {

        }

        @Override
        public void createModel() {

        }

        private void on(Event1 e) {
            throw new RuntimeException("expected exception 1");
        }

        private void on(Event2 e, MetaData meta) {
            throw new RuntimeException("expected exception 2");

        }
    }

    public static class Event1 implements Event {

    }

    public static class Event2 implements Event {

    }
}
