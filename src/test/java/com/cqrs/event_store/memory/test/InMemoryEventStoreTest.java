package com.cqrs.event_store.memory.test;

import com.cqrs.aggregates.AggregateDescriptor;
import com.cqrs.base.Event;
import com.cqrs.event_store.AggregateEventStream;
import com.cqrs.event_store.memory.InMemoryEventStore;
import com.cqrs.events.EventWithMetaData;
import com.cqrs.events.MetaData;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InMemoryEventStoreTest {

    @Test
    void loadEventsForAggregate() {
        InMemoryEventStore sut = new InMemoryEventStore();

        final AggregateDescriptor aggDsc123 = new AggregateDescriptor("123", "aggregateClass");
        AggregateEventStream aggStreamBefore = sut.loadEventsForAggregate(aggDsc123);
        assertEquals(0, aggStreamBefore.getVersion());
        assertEquals(0, aggStreamBefore.count());

        ArrayList<EventWithMetaData> someEvents = new ArrayList<EventWithMetaData>(){{
            add(new EventWithMetaData(factoryEvent(), new MetaData(LocalDateTime.now(), aggDsc123.aggregateId, aggDsc123.aggregateClass)));
        }};

        sut.appendEventsForAggregate(aggDsc123, someEvents, 0);

        assertThrows(ConcurrentModificationException.class, () -> {
            sut.appendEventsForAggregate(aggDsc123, someEvents, -1);
        });

        AggregateEventStream aggStreamAfter = sut.loadEventsForAggregate(aggDsc123);
        assertEquals(1, aggStreamAfter.getVersion());
        assertEquals(1, aggStreamAfter.count());
    }

    private Event factoryEvent() {
        return new Event() {
            @Override
            public int hashCode() {
                return super.hashCode();
            }
        };
    }
}