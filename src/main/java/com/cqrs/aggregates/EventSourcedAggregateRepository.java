package com.cqrs.aggregates;

import com.cqrs.base.Aggregate;
import com.cqrs.base.EventStore;
import com.cqrs.event_store.exceptions.StorageException;
import com.cqrs.events.EventWithMetaData;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class EventSourcedAggregateRepository implements AggregateRepository {

    final public EventStore eventStore;
    final private HashMap<String, Integer> loadedAggregateVersions = new HashMap<>();

    public EventSourcedAggregateRepository(
        EventStore eventStore
    ) {
        this.eventStore = eventStore;
    }

    private static Aggregate factoryAggregate(AggregateDescriptor aggregateDescriptor)
        throws AggregateTypeException {
        String aggregateClass = aggregateDescriptor.aggregateClass;
        try {
            Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(aggregateClass);
            Constructor<?> ctor = clazz.getDeclaredConstructor();
            ctor.setAccessible(true);
            return (Aggregate) ctor.newInstance();
        } catch (ClassNotFoundException | InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            throw new AggregateTypeException(aggregateDescriptor, e);
        }
    }

    @Override
    public Aggregate loadAggregate(AggregateDescriptor aggregateDescriptor)
        throws AggregateTypeException, AggregateExecutionException, StorageException {
        Aggregate aggregate = factoryAggregate(aggregateDescriptor);
        int lastVersion = eventStore.loadEventsForAggregate(aggregateDescriptor, eventWithMetaData -> {
            EventApplierOnAggregate.applyEvent(aggregate,
                eventWithMetaData.event,
                eventWithMetaData.metadata);
            return true;
        });
        loadedAggregateVersions.put(aggregateDescriptor.toString(), lastVersion);
        return aggregate;
    }

    @Override
    public List<EventWithMetaData> saveAggregate(
        String aggregateId,
        Aggregate aggregate,
        List<EventWithMetaData> newEventsWithMeta
    ) throws ConcurrentModificationException, StorageException {
        final AggregateDescriptor aggregateDescriptor = new AggregateDescriptor(
            aggregateId,
            aggregate.getClass().getCanonicalName()
        );
        int prevVersion = loadedAggregateVersions.get(aggregateDescriptor.toString());
        eventStore.appendEventsForAggregate(
            aggregateDescriptor, newEventsWithMeta, prevVersion
        );
        List<EventWithMetaData> decoratedEvents = new LinkedList<>();
        for (EventWithMetaData eventWithMetaData : newEventsWithMeta) {
            decoratedEvents.add(eventWithMetaData.withVersion(++prevVersion));
        }
        return decoratedEvents;
    }
}
