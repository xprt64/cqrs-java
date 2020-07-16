package com.cqrs.aggregates.SomeAggregateBddTestShouldThrow.SomeAggregateBddTestThrows;

import com.cqrs.base.Aggregate;
import com.cqrs.base.Command;
import com.cqrs.commands.CommandHandlerDescriptor;
import com.cqrs.testing.BddAggregateTestHelper;
import com.cqrs.testing.exceptions.NoExceptionThrown;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SomeAggregateBddTest {
    @Test
    public void testThrowsExpectedException() {
        Aggregate1 sut = new Aggregate1();

        BddAggregateTestHelper helper = new BddAggregateTestHelper(commandClass -> new CommandHandlerDescriptor(Aggregate1.class.getCanonicalName(), "handle"));

        try{
            helper.onAggregate(sut)
                .given()
                .when(new Command1("456"))
                .thenThrows(MyException.class);
            fail("should throw exception when aggregate is expected to throw but doesn't");
        }
        catch (NoExceptionThrown e){
            //pass
        }
     }
}

class Aggregate1 extends Aggregate {
    public void handle(Command1 command) throws MyException {

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