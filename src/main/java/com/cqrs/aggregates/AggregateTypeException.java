package com.cqrs.aggregates;

public class AggregateTypeException extends RuntimeException {

    public final AggregateDescriptor aggregateDescriptor;

    public AggregateTypeException(AggregateDescriptor aggregateDescriptor, Throwable cause)
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
