package com.cqrs.aggregates;

import com.cqrs.base.Aggregate;
import com.cqrs.base.Event;
import com.cqrs.events.MetaData;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import static com.cqrs.aggregates.EventApplierOnAggregate.applyEvent;

import static org.junit.jupiter.api.Assertions.*;

class EventApplierOnAggregateTest {
    @Test
    public void applyEventTest() {
        AtomicReference<ArrayList<Event>> actualAppliedEvents = new AtomicReference<>(new ArrayList<>());
        Aggregate aggregate = new Aggregate() {
            public void apply(Event1 event) {
                actualAppliedEvents.get().add(event);
            }

            public void apply(Event2 event, MetaData m) {
                actualAppliedEvents.get().add(event);
            }
        };
        applyEvent(
            aggregate,
            new Event1(),
            factoryMetaData()
        );
        applyEvent(
            aggregate,
            new Event2(),
            factoryMetaData()
        );

        assertEquals(2, actualAppliedEvents.get().size());
    }

    private MetaData factoryMetaData() {
        return new MetaData(LocalDateTime.now(), "agg1", "aggClas", null, 1, null, "ev1");
    }

    public static class Event1 implements Event {

    }

    public static class Event2 implements Event {

    }
}