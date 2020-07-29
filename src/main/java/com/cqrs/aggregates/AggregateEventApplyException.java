package com.cqrs.aggregates;

import com.cqrs.base.Aggregate;
import com.cqrs.base.Event;

public class AggregateEventApplyException extends RuntimeException {

    public final Aggregate aggregate;
    private final Event event;
    private final String eventId;

    public AggregateEventApplyException(Aggregate aggregate, Event event, String eventId, Throwable cause) {
        super(cause);
        this.aggregate = aggregate;
        this.event = event;
        this.eventId = eventId;
    }

    @Override
    public String toString() {
        return "Aggregate " + aggregate.getClass().getCanonicalName() + " threw " + getCause()
            .getClass().getCanonicalName() + " while applying event " + event.getClass().getCanonicalName() + "#" + eventId + " with message: " + getCause().getMessage();

    }
}
