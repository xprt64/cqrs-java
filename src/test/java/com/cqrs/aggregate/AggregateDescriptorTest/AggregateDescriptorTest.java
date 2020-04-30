package com.cqrs.aggregate.AggregateDescriptorTest;

import com.cqrs.aggregates.AggregateDescriptor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AggregateDescriptorTest
{
    @Test
    void testToString()
    {
        AggregateDescriptor sut = new AggregateDescriptor("123", "someClass");
        assertEquals(sut.aggregateId, "123");
    }
}
