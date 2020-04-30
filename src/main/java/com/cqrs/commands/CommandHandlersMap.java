package com.cqrs.commands;

import java.util.HashMap;

public interface CommandHandlersMap {
    HashMap<String, String[]> getMap();
}
