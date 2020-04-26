package com.dudulina.aggregate.AggregateDescriptorTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.dudulina.aggregates.AggregateDescriptor;
import com.dudulina.aggregates.AggregateId;
import org.junit.jupiter.api.Test;

class AggregateDescriptorTest
{
    @Test
    void testToString()
    {
        AggregateDescriptor sut = new AggregateDescriptor(new AggregateIdTest("123"), "someClass");
        assertEquals(sut.aggregateId.__toString(), "123");
    }
}

class AggregateIdTest implements AggregateId
{

    public final String id;

    public AggregateIdTest(String id)
    {
        this.id = id;
    }

    @Override
    public String __toString()
    {
        return id;
    }
}