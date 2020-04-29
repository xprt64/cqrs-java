package com.dudulina.command;

import static org.junit.jupiter.api.Assertions.*;

import com.dudulina.aggregates.AggregateId;
import com.dudulina.base.Command;
import java.util.HashMap;
import org.junit.jupiter.api.Test;

class CommandSubscriberByMapTest {

    @Test
    void getAggregateForCommand() throws CommandHandlerNotFound {

        CommandSubscriberByMap sut = new CommandSubscriberByMap(new CommandHandlersMap() {
            @Override
            public HashMap<String, String[]> getMap() {
                HashMap<String, String[]> map = new HashMap<String, String[]>();
                map.put(CommandSubscriberByMapTestCommand.class.getCanonicalName(), new String[]{"a", "b"});
                return map;
            }
        });
        final CommandHandlerDescriptor aggregateForCommand = sut.getAggregateForCommand(CommandSubscriberByMapTestCommand.class);
        assertEquals("a", aggregateForCommand.aggregateClass);
        assertEquals("b", aggregateForCommand.methodName);
    }

    @Test
    void getAggregateForCommandWithException() {
        assertThrows(CommandHandlerNotFound.class, () -> {
            CommandSubscriberByMap sut = new CommandSubscriberByMap(new CommandHandlersMap() {
                @Override
                public HashMap<String, String[]> getMap() {
                    return new HashMap<String, String[]>();
                }
            });
            sut.getAggregateForCommand(CommandSubscriberByMapTestCommand.class);
        });
    }
}

class CommandSubscriberByMapTestCommand implements Command{

    @Override
    public AggregateId getAggregateId() {
        return null;
    }
}