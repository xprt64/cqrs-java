package com.cqrs.read_model;

import java.util.List;

public interface ReadModelReflector {

    List<String> getEventClassesFromReadModel(String readModelClass);

    String getEventHandlerMethodNameForEvent(String readModelClass, String eventClass);
}
