package com.dudulina.command;

import com.dudulina.events.EventWithMetaData;
import java.util.List;

public interface SideEffectsDispatcher {
    public void dispatchSideEffects(List<EventWithMetaData> sideEffects);
}
