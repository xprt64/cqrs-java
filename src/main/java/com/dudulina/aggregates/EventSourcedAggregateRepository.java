package com.dudulina.aggregates;

import com.dudulina.base.Aggregate;
import com.dudulina.base.EventStore;
import com.dudulina.event_store.AggregateEventStream;
import com.dudulina.events.EventWithMetaData;
import com.dudulina.events.EventsApplierOnAggregate;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class EventSourcedAggregateRepository implements AggregateRepository {

    final public EventStore eventStore;
    final public EventsApplierOnAggregate eventsApplierOnAggregate;
    final private HashMap<String, AggregateEventStream> aggregateToEventStreamMap = new HashMap<>();

    public EventSourcedAggregateRepository(
        EventStore eventStore,
        EventsApplierOnAggregate eventsApplierOnAggregate)
    {
        this.eventStore = eventStore;
        this.eventsApplierOnAggregate = eventsApplierOnAggregate;
    }

    @Override
    public Aggregate loadAggregate(AggregateDescriptor aggregateDescriptor)
        throws AggregateException, AggregateExecutionException
    {
        Aggregate aggregate = factoryAggregate(aggregateDescriptor);
        AggregateEventStream priorEvents = eventStore.loadEventsForAggregate(aggregateDescriptor);
        aggregateToEventStreamMap.put(aggregateDescriptor.toString(), priorEvents);
        eventsApplierOnAggregate.applyEventsOnAggregate(aggregate, iteratorToList(priorEvents));
        return aggregate;
    }

    private static Aggregate factoryAggregate(AggregateDescriptor aggregateDescriptor)
        throws AggregateException
    {
        String aggregateClass = aggregateDescriptor.aggregateClass;
        try {
            Class<?> clazz = Class.forName(aggregateClass);
            Constructor<?> ctor = clazz.getDeclaredConstructor();
            ctor.setAccessible(true);
            return (Aggregate) ctor.newInstance();
        } catch (ClassNotFoundException | InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            throw new AggregateException(aggregateDescriptor, e);
        }
    }

    private List<EventWithMetaData> iteratorToList(AggregateEventStream iterator)
    {
        List<EventWithMetaData> actualList = new ArrayList<>();
        while (iterator.hasNext()) {
            actualList.add(iterator.next());
        }
        return actualList;
    }

    @Override
    public List<EventWithMetaData> saveAggregate(AggregateId aggregateId, Aggregate aggregate,
        List<EventWithMetaData> newEventsWithMeta) throws ConcurrentModificationException
    {
        final AggregateDescriptor aggregateDescriptor = new AggregateDescriptor(aggregateId,
            aggregate.getClass().getCanonicalName());
        AggregateEventStream priorEvents = aggregateToEventStreamMap.get(
            aggregateDescriptor.toString());

        eventStore.appendEventsForAggregate(
            aggregateDescriptor, newEventsWithMeta, priorEvents.getVersion()
        );
        return newEventsWithMeta.stream()
            .map(temp -> temp.withVersion(priorEvents.getVersion() + 1))
            .collect(Collectors.toList());
    }
}
