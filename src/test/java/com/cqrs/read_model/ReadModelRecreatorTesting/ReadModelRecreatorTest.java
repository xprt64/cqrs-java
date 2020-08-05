package com.cqrs.read_model.ReadModelRecreatorTesting;

import com.cqrs.aggregates.AggregateDescriptor;
import com.cqrs.base.Event;
import com.cqrs.base.EventStore;
import com.cqrs.event_store.exceptions.StorageException;
import com.cqrs.events.EventWithMetaData;
import com.cqrs.events.MetaData;
import com.cqrs.read_model.OnlyOnceTracker;
import com.cqrs.read_model.ReadModel;
import com.cqrs.read_model.ReadModelEventApplier;
import com.cqrs.read_model.ReadModelRecreator;
import com.cqrs.read_model.ReadModelReflector;
import com.cqrs.util.Guid;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

class ReadModelRecreatorTest {

    @Test
    void recreateRead() {
        AtomicInteger actualSteps = new AtomicInteger(-1);
        AtomicReference<ArrayList<Event>> actualAppliedEvents = new AtomicReference<>(new ArrayList<>());
        AtomicBoolean cleared = new AtomicBoolean(false);
        AtomicBoolean created = new AtomicBoolean(false);
        AtomicReference<List<String>> actualEventClasses = new AtomicReference<>(new ArrayList<>());

        EventStore eventStore = getEventStore(actualEventClasses, wrapEvent(new Event1(1)), wrapEvent(new Event2(2)), wrapEvent(new Event1(3)));
        ReadModelRecreator sut = new ReadModelRecreator(
            eventStore,
            null,
            (currentStep, steps, speedInItemsPerSec, etaInSeconds) -> actualSteps.set(steps),
            new ReadModelEventApplier(
                new OnlyOnceTracker(),
                (readModel, methodName, eventWithMetadata, exception) -> {
                    fail(exception.getMessage());
                }
            ),
            new ReadModelReflector() {
                @Override
                public List<String> getEventClassesFromReadModel(String readModelClass) {
                    return Arrays.asList(Event1.class.getCanonicalName(), Event2.class.getCanonicalName());
                }

                @Override
                public String getEventHandlerMethodNameForEvent(String readModelClass, String eventClass) {
                    return "on";
                }
            }
        );

        sut.recreateRead(new ReadModel() {
            @Override
            public void clearModel() {
                cleared.set(true);
            }

            @Override
            public void createModel() {
                created.set(true);
            }

            public void on(Event1 event) {
                actualAppliedEvents.get().add(event);
            }

            public void on(Event2 event) {
                actualAppliedEvents.get().add(event);
            }

        });

        assertEquals(2, actualEventClasses.get().size());
        assertEquals(3, actualSteps.get());
        assertFalse(cleared.get());
        assertFalse(created.get());
        assertEquals(3, actualAppliedEvents.get().size());
    }

    private EventWithMetaData wrapEvent(Event event) {
        MetaData metaData = new MetaData(LocalDateTime.now(), "1", "aggClass");
        metaData = metaData.withEventId(Guid.generate());
        return new EventWithMetaData(event, metaData);
    }

    private EventStore getEventStore(AtomicReference<List<String>> usedEventClasses, EventWithMetaData... events) {
        return new EventStore() {

            @Override
            public int loadEventsForAggregate(AggregateDescriptor aggregateDescriptor, Predicate<EventWithMetaData> consumer) throws StorageException {
                fail("Should not be called");
                return 0;
            }

            @Override
            public void appendEventsForAggregate(AggregateDescriptor aggregateDescriptor, List<EventWithMetaData> eventsWithMetaData, int expectedVersion) throws ConcurrentModificationException, StorageException {
                fail("Should not be called");
            }

            @Override
            public void loadEventsByClassNames(List<String> eventClasses, Predicate<EventWithMetaData> consumer) throws StorageException {
                usedEventClasses.set(eventClasses);
                for(EventWithMetaData e:events){
                    if(!consumer.test(e)){
                        return;
                    }
                }
            }

            @Override
            public int countEventsByClassNames(List<String> eventClasses) throws StorageException {
                return events.length;
            }

            @Override
            public EventWithMetaData findEventById(String eventId) throws StorageException {
                fail("Should not be called");
                return null;
            }
        };
    }

    public static class Event1 implements Event {
        public final int int1;

        public Event1(int int1) {
            this.int1 = int1;
        }
    }

    public static class Event2 implements Event {
        public final int int1;

        public Event2(int int1) {
            this.int1 = int1;
        }
    }

    ;
}