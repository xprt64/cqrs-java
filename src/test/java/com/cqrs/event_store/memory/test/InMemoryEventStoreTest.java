package com.cqrs.event_store.memory.test;

import com.cqrs.aggregates.AggregateDescriptor;
import com.cqrs.base.Event;
import com.cqrs.event_store.exceptions.StorageException;
import com.cqrs.event_store.memory.InMemoryEventStore;
import com.cqrs.events.EventWithMetaData;
import com.cqrs.events.MetaData;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryEventStoreTest {

    @Test
    void loadEventsForAggregate() throws StorageException {
        InMemoryEventStore sut = new InMemoryEventStore();


        final AggregateDescriptor aggDsc123 = new AggregateDescriptor("123", "aggregateClass");

        int oldVersion = sut.loadEventsForAggregate(aggDsc123, eventWithMetaData -> true);

        ArrayList<EventWithMetaData> someEvents = new ArrayList<EventWithMetaData>(){{
            add(new EventWithMetaData(factoryEvent(), new MetaData(LocalDateTime.now(), aggDsc123.aggregateId, aggDsc123.aggregateClass)));
        }};

        sut.appendEventsForAggregate(aggDsc123, someEvents, oldVersion);

        int version = sut.loadEventsForAggregate(aggDsc123, eventWithMetaData -> true);
        assertEquals(oldVersion+1, version);
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