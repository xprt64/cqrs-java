package com.dudulina.command.DefaultCommandDispatcherTest.dispatchCommand;

import com.dudulina.aggregates.AggregateDescriptor;
import com.dudulina.aggregates.AggregateException;
import com.dudulina.aggregates.AggregateExecutionException;
import com.dudulina.aggregates.AggregateId;
import com.dudulina.aggregates.AggregateRepository;
import com.dudulina.base.Aggregate;
import com.dudulina.base.Command;
import com.dudulina.base.Event;
import com.dudulina.command.CommandApplier;
import com.dudulina.command.CommandHandlerDescriptor;
import com.dudulina.command.CommandWithMetadata;
import com.dudulina.command.ConcurrentProofFunctionCaller;
import com.dudulina.command.DefaultCommandDispatcher;
import com.dudulina.command.MetadataWrapper;
import com.dudulina.events.EventWithMetaData;
import com.dudulina.events.MetaData;
import com.dudulina.events.MetadataFactory;
import com.dudulina.testing.BddAggregateTestHelper;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DefaultCommandDispatcherTest
{
    private boolean aggregateSaved = false;
    private List<EventWithMetaData> sideEffectsDispatched;

    @Test
    void dispatchCommand()
        throws Exception
    {
        DefaultCommandDispatcher sut = new DefaultCommandDispatcher(
            commandClass -> new CommandHandlerDescriptor(Aggregate1.class.getCanonicalName(), "handle"),
            new CommandApplier(),
            getAggregateRepository(),
            new ConcurrentProofFunctionCaller<>(),
            new MetadataFactory()
            {
            },
            new MetadataWrapper(),
            sideEffects -> sideEffectsDispatched = sideEffects
        );

        sut.dispatchCommand(new Command1(new AggregateId1("123")), null);
        assertTrue(aggregateSaved);
        //assertNotNull(sideEffectsDispatched);
//        BddAggregateTestHelper.assertEventListsAreEqual(
//            sideEffectsDispatched.stream().map(eventWithMetaData -> eventWithMetaData.event).collect(Collectors.toList()),
//            new LinkedList<>(Collections.singletonList(new Event1("some data")))
//        );
    }

    @NotNull
    private AggregateRepository getAggregateRepository()
    {
        return new AggregateRepository()
        {
            @Override
            public Aggregate loadAggregate(AggregateDescriptor aggregateDescriptor) throws AggregateException, AggregateExecutionException
            {
                return new Aggregate1();
            }

            @Override
            public List<EventWithMetaData> saveAggregate(
                AggregateId aggregateId, Aggregate aggregate, List<EventWithMetaData> newEventsWithMeta
            )
            {
                aggregateSaved = true;
                return newEventsWithMeta;
            }
        };
    }
}


class Aggregate1 extends Aggregate
{
    private String appliedEventData;

    public void handle(Command1 command)
    {
        emit(new Event1("some data"));
    }

    public void apply(Event1 event)
    {
        appliedEventData = event.data;
    }

    public String getAppliedEventData()
    {
        return appliedEventData;
    }
}

class Command1 implements Command
{

    public final AggregateId1 aggregateId;

    public Command1(AggregateId1 aggregateId)
    {
        this.aggregateId = aggregateId;
    }

    @Override
    public AggregateId getAggregateId()
    {
        return aggregateId;
    }
}

class Event1 implements Event
{
    public final String data;

    public Event1(String data)
    {
        this.data = data;
    }
}

class AggregateId1 implements AggregateId
{
    public final String id;

    public AggregateId1(String id)
    {
        this.id = id;
    }

    @Override
    public String __toString()
    {
        return id;
    }
}