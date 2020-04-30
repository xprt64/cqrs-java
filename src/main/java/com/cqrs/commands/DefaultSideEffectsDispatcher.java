package com.cqrs.commands;

import com.cqrs.events.EventDispatcher;
import com.cqrs.events.EventWithMetaData;
import java.util.List;
import java.util.Objects;

public class DefaultSideEffectsDispatcher implements SideEffectsDispatcher {

    private final EventDispatcher eventDispatcher;

    public DefaultSideEffectsDispatcher(EventDispatcher eventDispatcher)
    {
        this.eventDispatcher = eventDispatcher;
    }

    @Override
    public void dispatchSideEffects(List<EventWithMetaData> sideEffects)
    {
        Objects.requireNonNull(sideEffects);
        sideEffects.forEach(this.eventDispatcher::dispatchEvent);
    }
}
