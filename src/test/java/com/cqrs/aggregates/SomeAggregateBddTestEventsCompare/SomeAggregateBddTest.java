package com.cqrs.aggregates.SomeAggregateBddTestEventsCompare;

import com.cqrs.annotations.MessageHandler;
import com.cqrs.base.Aggregate;
import com.cqrs.base.Command;
import com.cqrs.base.Event;
import com.cqrs.testing.BddAggregateTestHelper;
import com.cqrs.testing.exceptions.ExpectedEventNotYielded;
import com.cqrs.testing.exceptions.TooManyEventsFired;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class SomeAggregateBddTest {

    private BddAggregateTestHelper helper;

    @BeforeEach
    void setUp() {
        helper = new BddAggregateTestHelper(commandClass -> new MessageHandler(Aggregate1.class.getCanonicalName(), "handle"));
    }

    @Test
    public void testEventsAreCorrectlyCompared() {

        helper.onAggregate(new Aggregate1())
            .when(new Command1("123", Arrays.asList("a", "b")))
            .then(new Event1(Arrays.asList("a", "b")));
    }

    @Test
    public void testExpectedEventNotYielded() {
        assertThrows(ExpectedEventNotYielded.class, () -> {
            helper.onAggregate(new Aggregate1())
                .when(new Command1("123", Arrays.asList("a", "b")))
                .then(new Event1(Arrays.asList("a", "b", "extra")));
        });
    }

    @Test
    public void testTooManyEventsFired() {
        assertThrows(TooManyEventsFired.class, () -> {
            helper.onAggregate(new Aggregate1())
                .when(new Command1("123", Arrays.asList("a", "b", "extra")))
                .then();
        });
    }
}

class Aggregate1 extends Aggregate {
    public void handle(Command1 command) {
        emit(new Event1(command.data.stream().filter(s -> true).collect(Collectors.toList())));
    }

    public void apply(Event1 event) {
    }
}

class Command1 implements Command {
    public final String aggregateId;
    public final List<String> data;

    public Command1(String aggregateId, List<String> data) {
        this.aggregateId = aggregateId;
        this.data = data;
    }

    @Override
    public String getAggregateId() {
        return aggregateId;
    }
}

class Event1 implements Event {
    public final List<String> data;

    public Event1(List<String> data) {
        this.data = data;
    }
}
