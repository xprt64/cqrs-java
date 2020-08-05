package com.cqrs.read_model;

import com.cqrs.base.Event;
import com.cqrs.events.EventWithMetaData;
import com.cqrs.events.MetaData;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class ReadModelEventApplierTest {

    @Test
    void applyEventOnlyOnce() {
        ReadModelEventApplier sut = new ReadModelEventApplier(
            new OnlyOnceTracker(),
            (readModel, methodName, eventWithMetadata, exception) -> fail("should not fail but failed with " + exception.getMessage(), exception)
        );
        RM readModel = new RM();
        sut.applyEventOnlyOnce(readModel, "on", new EventWithMetaData(new Event1(), new MetaData(null, "agg1", "aggC", null, 1, null, "id1")));
        //again, same event ID
        sut.applyEventOnlyOnce(readModel, "on", new EventWithMetaData(new Event1(), new MetaData(null, "agg1", "aggC", null, 1, null, "id1")));
        //event2, ID2
        sut.applyEventOnlyOnce(readModel, "on", new EventWithMetaData(new Event2(), new MetaData(null, "agg1", "aggC", null, 1, null, "id2")));

        assertEquals(2, readModel.appliedEvents.get().size());
    }

    public static class RM implements ReadModel {

        public AtomicReference<ArrayList<Event>> appliedEvents = new AtomicReference<>(new ArrayList<>());

        @Override
        public void clearModel() {

        }

        @Override
        public void createModel() {

        }

        private void on(Event1 e) {
            appliedEvents.get().add(e);
        }

        private void on(Event2 e, MetaData meta) {
            appliedEvents.get().add(e);
        }
    }

    public static class Event1 implements Event {

    }

    public static class Event2 implements Event {

    }
}
