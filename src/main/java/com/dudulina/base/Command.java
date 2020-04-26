package com.dudulina.base;

import com.dudulina.aggregates.AggregateId;

public interface Command {
    public AggregateId getAggregateId();
}
