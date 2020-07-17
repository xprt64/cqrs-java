package com.cqrs.aggregates.SomeAggregateBddTestTwoEvents;

import com.cqrs.base.Aggregate;
import com.cqrs.base.Command;
import com.cqrs.base.Event;
import com.cqrs.commands.CommandHandlerDescriptor;
import com.cqrs.testing.BddAggregateTestHelper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SomeAggregateBddTestTwoEvents
{
    @Test
    public void testEventsAreCorrectlyEmittedAndApplied()
    {
        Aggregate1 sut = new Aggregate1();

        BddAggregateTestHelper helper = new BddAggregateTestHelper(commandClass -> new CommandHandlerDescriptor(Aggregate1.class.getCanonicalName(), "handle"));

        helper.onAggregate(sut)
              .when(new Command1(new String("123")))
              .then(new Event1("some data 1"), new Event2("some data 2"));

        assertEquals(sut.getAppliedEventData1(), "some data 1");
        assertEquals(sut.getAppliedEventData2(), "some data 2");
    }
}

class Aggregate1 extends Aggregate
{
    private String appliedEventData1;
    private String appliedEventData2;

    public void handle(Command1 command)
    {
        emit(new Event1("some data 1"));
        emit(new Event2("some data 2"));
    }

    public void apply(Event1 event)
    {
        appliedEventData1 = event.data;
    }

    public void apply(Event2 event)
    {
        appliedEventData2 = event.data;
    }

    public String getAppliedEventData1()
    {
        return appliedEventData1;
    }

    public String getAppliedEventData2()
    {
        return appliedEventData2;
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

class Event2 implements Event
{
    public final String data;

    public Event2(String data)
    {
        this.data = data;
    }
}
