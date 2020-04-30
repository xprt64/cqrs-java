package com.cqrs.reflection;

import java.util.List;

public interface ReadModelReflector {

    List<String> getEventClassesFromReadModel(Class<?> readModelClass);

    String getEventHandlerForEvent(Class<?> eventClass);
}
