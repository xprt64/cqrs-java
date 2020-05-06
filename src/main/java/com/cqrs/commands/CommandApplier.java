package com.cqrs.commands;

import com.cqrs.aggregates.AggregateExecutionException;
import com.cqrs.base.Aggregate;
import com.cqrs.base.Command;
import com.cqrs.base.Event;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class CommandApplier
{

    public List<Event> applyCommand(Aggregate aggregate, Command command, String methodName)
    throws AggregateExecutionException
    {
        aggregate.beginCommand();
        try {
            Method handle = aggregate.getClass().getDeclaredMethod(methodName, command.getClass());
            handle.setAccessible(true);
            handle.invoke(aggregate, command);
            return aggregate.endCommand();
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new AggregateExecutionException(aggregate, e);
        }
    }
}