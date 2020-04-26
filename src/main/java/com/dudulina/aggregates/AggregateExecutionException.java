package com.dudulina.aggregates;

import com.dudulina.base.Aggregate;

public class AggregateExecutionException extends Exception
{

    public final Aggregate aggregate;

    public AggregateExecutionException(Aggregate aggregate, Throwable cause)
    {
        super(cause);
        this.aggregate = aggregate;
    }

    @Override
    public String toString()
    {
        return "Aggregate " + aggregate.getClass().getCanonicalName() + " has problems: " + getCause()
            .getClass().getCanonicalName() + ": " + getCause().getMessage();
    }
}
