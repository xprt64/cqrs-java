package com.cqrs.commands.DefaultCommandDispatcherTest.dispatchCommand;

import com.cqrs.aggregates.AggregateDescriptor;
import com.cqrs.aggregates.AggregateRepository;
import com.cqrs.base.Aggregate;
import com.cqrs.base.Command;
import com.cqrs.base.Event;
import com.cqrs.commands.*;
import com.cqrs.events.EventWithMetaData;
import com.cqrs.events.MetadataFactory;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

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

        sut.dispatchCommand(new Command1("123"), null);
        assertTrue(aggregateSaved);
        //assertNotNull(sideEffectsDispatched);
//        BddAggregateTestHelper.assertEventListsAreEqual(
//            sideEffectsDispatched.stream().map(eventWithMetaData -> eventWithMetaData.event).collect(Collectors.toList()),
//            new LinkedList<>(Collections.singletonList(new Event1("some data")))
//        );
    }

    private AggregateRepository getAggregateRepository()
    {
        return new AggregateRepository()
        {
            @Override
            public Aggregate loadAggregate(AggregateDescriptor aggregateDescriptor)
            {
                return new Aggregate1();
            }

            @Override
            public List<EventWithMetaData> saveAggregate(
                String aggregateId, Aggregate aggregate, List<EventWithMetaData> newEventsWithMeta
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

    public final String aggregateId;

    public Command1(String aggregateId)
    {
        this.aggregateId = aggregateId;
    }

    @Override
    public String getAggregateId()
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
