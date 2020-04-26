package com.dudulina.aggregates;

public class AggregateException extends Exception {

    public final AggregateDescriptor aggregateDescriptor;

    public AggregateException(AggregateDescriptor aggregateDescriptor, Throwable cause)
    {
        super(cause);
        this.aggregateDescriptor = aggregateDescriptor;
    }

    @Override
    public String toString()
    {
        return "Aggregate " + aggregateDescriptor.aggregateClass + " + has problems: " + getCause()
            .getMessage();
    }
}
