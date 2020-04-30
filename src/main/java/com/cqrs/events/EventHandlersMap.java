package com.cqrs.events;

import java.util.HashMap;

public interface EventHandlersMap {
    HashMap<String, String[][]> getMap();
}
