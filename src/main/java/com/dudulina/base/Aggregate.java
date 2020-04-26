package com.dudulina.base;

import com.dudulina.aggregates.EventApplierOnAggregate;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

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
        EventApplierOnAggregate.applyEvent(this, event, null);
    }

    public List<Event> endCommand()
    {
        return events;
    }
}
