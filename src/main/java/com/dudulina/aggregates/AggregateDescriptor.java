package com.dudulina.aggregates;

public class AggregateDescriptor {
    public final AggregateId aggregateId;
    public final String aggregateClass;

    public AggregateDescriptor(AggregateId aggregateId, String aggregateClass)
    {
        this.aggregateId = aggregateId;
        this.aggregateClass = aggregateClass;
    }

    @Override
    public String toString()
    {
        return aggregateClass + aggregateId.__toString();
    }
}
