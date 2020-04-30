package com.cqrs.events;

import com.cqrs.base.Event;
import java.util.List;
import java.util.function.BiConsumer;

public interface EventSubscriber {

    List<BiConsumer<Event, MetaData>> getListenersForEvent(Event event);
}
