package com.dudulina.aggregates.SomeAggregateBddTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.dudulina.aggregates.AggregateId;
import com.dudulina.base.Aggregate;
import com.dudulina.base.Command;
import com.dudulina.base.Event;
import com.dudulina.command.CommandHandlerDescriptor;
import com.dudulina.testing.BddAggregateTestHelper;
import org.junit.jupiter.api.Test;

public class SomeAggregateBddTest
{
    @Test
    public void testEventsAreCorrectlyEmitedAndApplied() throws Exception
    {
        Aggregate1 sut = new Aggregate1();

        BddAggregateTestHelper helper = new BddAggregateTestHelper(commandClass -> new CommandHandlerDescriptor(Aggregate1.class.getCanonicalName(), "handle"));

        helper.onAggregate(sut)
              .when(new Command1(new AggregateId1("123")))
              .then(new Event1("some data"));

        assertEquals(sut.getAppliedEventData(), "some data");
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