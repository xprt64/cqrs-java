package com.cqrs.commands.CommandValidatorSubscriberByMapTesting.ok1;

import com.cqrs.annotations.HandlersMap;
import com.cqrs.annotations.MessageHandler;
import com.cqrs.base.Command;
import com.cqrs.commands.CommandValidatorSubscriberByMap;
import com.cqrs.commands.CommandWithMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommandValidatorSubscriberByMapTest {

    private CommandValidatorSubscriberByMap sut;

    @BeforeEach
    void setUp() {
        sut = new CommandValidatorSubscriberByMap(
            clazz -> {
                assertEquals(clazz.getCanonicalName(), MyValidator.class.getCanonicalName());
                return new MyValidator();
            },
            new HandlersMap() {
                @Override
                public HashMap<String, List<MessageHandler>> getMap() {
                    HashMap<String, List<MessageHandler>> result = new HashMap<>();
                    result.put(
                        MyCommand1.class.getCanonicalName(),
                        Collections.singletonList(
                            new MessageHandler(MyValidator.class.getCanonicalName(), "validate1")
                        )
                    );
                    result.put(
                        MyCommand2.class.getCanonicalName(),
                        Collections.singletonList(
                            new MessageHandler(MyValidator.class.getCanonicalName(), "validate2")
                        )
                    );
                    result.put(
                        MyCommand3.class.getCanonicalName(),
                        Collections.singletonList(
                            new MessageHandler(MyValidator.class.getCanonicalName(), "validate3")
                        )
                    );
                    return result;
                }
            }
        );
    }

    @Test
    void getListenersForCommandWhenReturnsException() {
        List<Function<CommandWithMetadata, List<Throwable>>> result = sut.getValidatorsForCommand(new MyCommand1());
        assertEquals(1, result.size());
        List<Throwable> errors = result.get(0).apply(new CommandWithMetadata(new MyCommand1(), null));
        assertEquals(1, errors.size());
        assertEquals("some error 1", errors.get(0).getMessage());
    }

    @Test
    void getListenersForCommandWhenThrowsException() {
        List<Function<CommandWithMetadata, List<Throwable>>> result = sut.getValidatorsForCommand(new MyCommand2());
        assertEquals(1, result.size());
        List<Throwable> errors = result.get(0).apply(new CommandWithMetadata(new MyCommand2(), null));
        assertEquals(1, errors.size());
        assertEquals("some error 2", errors.get(0).getMessage());
    }

    @Test
    void getListenersForCommandWhenReturnsListOfThroables() {
        List<Function<CommandWithMetadata, List<Throwable>>> result = sut.getValidatorsForCommand(new MyCommand3());
        assertEquals(1, result.size());
        List<Throwable> errors = result.get(0).apply(new CommandWithMetadata(new MyCommand3(), null));
        assertEquals(1, errors.size());
        assertEquals("some error 3", errors.get(0).getMessage());
    }
}

class MyValidator {
    public Exception validate1(MyCommand1 command) {
        return new Exception("some error 1");
    }

    public void validate2(MyCommand2 command) throws Exception {
        throw new Exception("some error 2");
    }

    public List<Throwable> validate3(MyCommand3 command) {
        return Collections.singletonList(new Exception("some error 3"));
    }
}

class MyCommand1 implements Command {

    @Override
    public String getAggregateId() {
        return "123";
    }
}

class MyCommand2 implements Command {

    @Override
    public String getAggregateId() {
        return "123";
    }
}

class MyCommand3 implements Command {

    @Override
    public String getAggregateId() {
        return "123";
    }
}