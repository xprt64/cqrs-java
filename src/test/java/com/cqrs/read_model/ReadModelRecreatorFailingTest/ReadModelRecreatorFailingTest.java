package com.cqrs.read_model.ReadModelRecreatorFailingTest;

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
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

class ReadModelRecreatorTest {

    @Test
    void recreateRead() {
        AtomicBoolean event2Applied = new AtomicBoolean(false);
        AtomicReference<Throwable> actualException = new AtomicReference<>();
        AtomicReference<ArrayList<String>> actualLoggedMessages = new AtomicReference<>(new ArrayList<>());

        EventStore eventStore = getEventStore(wrapEventUnique(new Event1(1)), wrapEventUnique(new Event2(2)), wrapEventUnique(new Event1(3)));
        ReadModelRecreator sut = new ReadModelRecreator(
            eventStore,
            msg -> actualLoggedMessages.get().add(msg),
            null,
            new ReadModelEventApplier(
                new OnlyOnceTracker(),
                (readModel, methodName, eventWithMetadata, exception) -> {
                    actualException.set(exception);
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

            }

            @Override
            public void createModel() {

            }

            public void on(Event1 event) {
                throw new RuntimeException("expected exception");
            }

            public void on(Event2 event, MetaData metaData) {
                event2Applied.set(true);
            }

        });

        assertNotNull(actualException.get());
        assertEquals("expected exception", actualException.get().getCause().getMessage());
        assertTrue(event2Applied.get());
        assertNotEquals(0, actualLoggedMessages.get().size());
    }

    private EventWithMetaData wrapEventUnique(Event event) {
        MetaData metaData = new MetaData(LocalDateTime.now(), "1", "aggClass");
        metaData = metaData.withEventId(Guid.generate());
        return new EventWithMetaData(event, metaData);
    }

    private EventStore getEventStore(EventWithMetaData... events) {
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
                for (EventWithMetaData e : events) {
                    if (!consumer.test(e)) {
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