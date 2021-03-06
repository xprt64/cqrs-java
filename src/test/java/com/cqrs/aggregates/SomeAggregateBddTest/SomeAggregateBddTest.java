package com.cqrs.aggregates.SomeAggregateBddTest;

import com.cqrs.annotations.MessageHandler;
import com.cqrs.base.Aggregate;
import com.cqrs.base.Command;
import com.cqrs.base.Event;
import com.cqrs.testing.BddAggregateTestHelper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SomeAggregateBddTest
{
    @Test
    public void testEventsAreCorrectlyEmittedAndApplied()
    {
        Aggregate1 sut = new Aggregate1();

        BddAggregateTestHelper helper = new BddAggregateTestHelper(commandClass -> new MessageHandler(Aggregate1.class.getCanonicalName(), "handle"));

        helper.onAggregate(sut)
              .when(new Command1("123"))
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
