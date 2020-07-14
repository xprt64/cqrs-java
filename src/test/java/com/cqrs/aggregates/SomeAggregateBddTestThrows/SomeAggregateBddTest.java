package com.cqrs.aggregates.SomeAggregateBddTestThrows;

import com.cqrs.base.Aggregate;
import com.cqrs.base.Command;
import com.cqrs.base.Event;
import com.cqrs.commands.CommandHandlerDescriptor;
import com.cqrs.testing.BddAggregateTestHelper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SomeAggregateBddTest {
    @Test
    public void testThrowsExpectedException() throws Exception {
        Aggregate1 sut = new Aggregate1();

        BddAggregateTestHelper helper = new BddAggregateTestHelper(commandClass -> new CommandHandlerDescriptor(Aggregate1.class.getCanonicalName(), "handle"));

        helper.onAggregate(sut)
                .given()
                .when(new Command1("456"))
                .thenThrows(MyException.class);
    }
}

class Aggregate1 extends Aggregate {
    public void handle(Command1 command) throws MyException {
        throw new MyException();
    }
}

class Command1 implements Command {

    public final String aggregateId;

    public Command1(String aggregateId) {
        this.aggregateId = aggregateId;
    }

    @Override
    public String getAggregateId() {
        return aggregateId;
    }
}

class MyException extends Exception {

}