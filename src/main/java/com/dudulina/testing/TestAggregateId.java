package com.dudulina.testing;

import com.dudulina.aggregates.AggregateId;
import com.dudulina.util.Guid;

public class TestAggregateId implements AggregateId
{
    private final String id;

    public TestAggregateId(String id)
    {
        this.id = id;
    }

    public static TestAggregateId random()
    {
        return new TestAggregateId(Guid.generate());
    }

    @Override
    public String __toString()
    {
        return id;
    }
}
