package com.cqrs.commands;

import com.cqrs.events.EventWithMetaData;
import java.util.List;

public interface SideEffectsDispatcher {
    public void dispatchSideEffects(List<EventWithMetaData> sideEffects);
}
