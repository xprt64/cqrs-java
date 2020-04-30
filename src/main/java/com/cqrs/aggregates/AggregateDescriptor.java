package com.cqrs.aggregates;

public class AggregateDescriptor {
    public final String aggregateId;
    public final String aggregateClass;

    public AggregateDescriptor(String aggregateId, String aggregateClass)
    {
        this.aggregateId = aggregateId;
        this.aggregateClass = aggregateClass;
    }

    @Override
    public String toString()
    {
        return aggregateClass + aggregateId;
    }
}
