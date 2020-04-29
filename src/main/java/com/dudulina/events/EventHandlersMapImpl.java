package com.dudulina.events;

import java.util.HashMap;

public class EventHandlersMapImpl implements EventHandlersMap{

    private final static HashMap<String, String[][]> map = new HashMap<>();

    static
    {
        map.put("clasa eveniment", new String[][]{
                new String[]{"a1", "b1"},
                new String[]{"a2", "b2"},
        });
    }
    @Override
    public HashMap<String, String[][]> getMap() {
        return map;
    }
}
