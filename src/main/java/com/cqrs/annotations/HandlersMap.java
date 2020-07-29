package com.cqrs.annotations;

import java.util.HashMap;
import java.util.List;

public interface HandlersMap {
    HashMap<String, List<MessageHandler>> getMap();

}
