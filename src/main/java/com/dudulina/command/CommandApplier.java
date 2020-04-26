package com.dudulina.command;

import com.dudulina.aggregates.AggregateExecutionException;
import com.dudulina.base.Aggregate;
import com.dudulina.base.Command;
import com.dudulina.base.Event;
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
            Method handle = aggregate.getClass().getMethod(methodName, command.getClass());
            handle.setAccessible(true);
            handle.invoke(aggregate, command);
            return aggregate.endCommand();
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new AggregateExecutionException(aggregate, e);
        }
    }
}
