package com.dudulina.events;

import com.dudulina.base.Event;
import java.util.function.BiConsumer;

public interface ErrorReporter {

    public void reportEventDispatchError(
        BiConsumer<Event, MetaData> listener,
        EventWithMetaData eventWithMetaData,
        Throwable throwable
    );
}
