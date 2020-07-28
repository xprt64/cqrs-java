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
//            System.out.println("Loader for " + aggregate.getClass().getCanonicalName());
//            System.out.println(aggregate.getClass().getClassLoader());
//            System.out.println("Loader for " + command.getClass().getCanonicalName());
//            System.out.println(command.getClass().getClassLoader());
            Method handle = aggregate.getClass().getDeclaredMethod(methodName, command.getClass());
            handle.setAccessible(true);
            handle.invoke(aggregate, command);
            return aggregate.endCommand();
        } catch (InvocationTargetException e) {
            throw new AggregateExecutionException(aggregate, e.getCause());
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new AggregateExecutionException(aggregate, e);
        }
    }
}
