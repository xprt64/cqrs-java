package com.cqrs.base;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static com.cqrs.aggregates.EventApplierOnAggregate.applyEvent;

public class Aggregate
{
    public static final String METHOD_NAME = "apply";
    private LinkedList<Event> events;

    public void beginCommand()
    {
        events = new LinkedList<>();
    }

    protected void emit(Event event)
    {
        Objects.requireNonNull(event);
        events.add(event);
        applyEvent(this, event, null);
    }

    public List<Event> endCommand()
    {
        return events;
    }
}
