package com.dudulina.events;

import com.dudulina.base.Event;
import java.util.List;
import java.util.function.BiConsumer;

public interface EventSubscriber {

    List<BiConsumer<Event, MetaData>> getListenersForEvent(Event event);
}