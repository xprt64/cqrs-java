package com.dudulina.testing;

import com.dudulina.aggregates.AggregateExecutionException;
import com.dudulina.aggregates.AggregateId;
import com.dudulina.aggregates.EventApplierOnAggregate;
import com.dudulina.base.Aggregate;
import com.dudulina.base.Command;
import com.dudulina.base.Event;
import com.dudulina.command.CommandApplier;
import com.dudulina.command.CommandHandlerDescriptor;
import com.dudulina.command.CommandHandlerNotFound;
import com.dudulina.command.CommandSubscriber;
import com.dudulina.events.EventWithMetaData;
import com.dudulina.events.MetaData;
import com.dudulina.testing.exceptions.ExpectedEventNotYielded;
import com.dudulina.testing.exceptions.TooManyEventsFired;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BddAggregateTestHelper
{
    private final CommandApplier commandApplier;
    private final CommandSubscriber commandSubscriber;
    private AggregateId aggregateId;
    private List<EventWithMetaData> priorEvents = new LinkedList<>();
    private Command command;
    private Aggregate aggregate;
    static ObjectMapper objectMapper = new ObjectMapper();

    public BddAggregateTestHelper(
        CommandSubscriber commandSubscriber
    )
    {
        this.commandSubscriber = commandSubscriber;
        commandApplier = new CommandApplier();
    }

    public static void assertEventListsAreEqual(List<Event> expectedEvents, List<Event> actualEvents) throws ExpectedEventNotYielded, TooManyEventsFired
    {
        List<String> expected = expectedEvents.stream().map(BddAggregateTestHelper::hashEvent).collect(Collectors.toList());
        List<String> actual = actualEvents.stream().map(BddAggregateTestHelper::hashEvent).collect(Collectors.toList());

        final ArrayList<String> tooFew = new ArrayList<String>(expected);
        tooFew.removeAll(actual);
        if (tooFew.size() > 0) {
            throw new ExpectedEventNotYielded("Expected events not emited: " + tooFew.toString());
        }

        final ArrayList<String> tooMany = new ArrayList<String>(actual);
        tooMany.removeAll(expected);
        if (tooMany.size() > 0) {
            throw new TooManyEventsFired("To many events emited: " + tooMany.toString());
        }
    }

    public static String hashEvent(Event event)
    {
        try {
            return event.getClass().getCanonicalName() + ":" + objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            return "Serialize error for " + event.getClass().getCanonicalName() + ":" + e.getMessage();
        }
    }

    private boolean isClassOrSubClass(Class<?> parentClass, Class<?> childClass)
    {
        return parentClass.isAssignableFrom(childClass);
    }

    public BddAggregateTestHelper onAggregate(Aggregate aggregate)
    {
        this.aggregate = aggregate;
        aggregateId = new TestAggregateId("123");
        return this;
    }

    public void given(Event... priorEvents)
    {
        this.priorEvents = Arrays.stream(priorEvents).map(this::decorateEventWithMetaData).collect(Collectors.toList());
    }

    public BddAggregateTestHelper when(Command command)
    {
        this.command = command;
        return this;
    }

    public void then(Event... expectedEvents) throws TooManyEventsFired, ExpectedEventNotYielded, CommandHandlerNotFound, AggregateExecutionException
    {
        Objects.requireNonNull(command);

        priorEvents.forEach(eventWithMetaData -> EventApplierOnAggregate.applyEvent(aggregate, eventWithMetaData.event, eventWithMetaData.metadata));

        List<Event> newEvents = executeCommand(command);

        assertTheseEvents(Arrays.asList(expectedEvents), newEvents);
    }

    public List<Event> executeCommand(Command $command) throws CommandHandlerNotFound, AggregateExecutionException
    {
        CommandHandlerDescriptor handler = commandSubscriber.getAggregateForCommand(command.getClass());

        return commandApplier.applyCommand(aggregate, $command, handler.methodName);
    }

    private EventWithMetaData decorateEventWithMetaData(Event event)
    {
        return new EventWithMetaData(event, factoryMetaData());
    }

    public void assertTheseEvents(List<Event> expectedEvents, List<Event> actualEvents) throws TooManyEventsFired, ExpectedEventNotYielded
    {
        assertEventListsAreEqual(expectedEvents, actualEvents);
        checkForToManyEvents(actualEvents.size() - expectedEvents.size());
    }

    private void checkForToManyEvents(int additionalCount) throws TooManyEventsFired
    {
        if (additionalCount > 0) {
            throw new TooManyEventsFired(
                String.format("Additional %d events fired", additionalCount));
        }
    }

    private MetaData factoryMetaData()
    {
        return new MetaData(
            LocalDateTime.now(),
            aggregateId,
            aggregate.getClass().getCanonicalName()
        );
    }
}
