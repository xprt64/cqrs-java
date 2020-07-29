package com.cqrs.questions;

import com.cqrs.annotations.HandlersMap;
import com.cqrs.annotations.MessageHandler;

import java.util.LinkedList;
import java.util.List;

public class AnswererResolverByMap implements AnswererResolver {

    private final HandlersMap handlersMap;

    public AnswererResolverByMap(
        HandlersMap handlersMap
    ) {
        this.handlersMap = handlersMap;
    }

    @Override
    public MessageHandler findAnswerer(Object question) {
        List<MessageHandler> handlers = handlersMap.getMap().getOrDefault(question.getClass().getCanonicalName(), new LinkedList<>());
        return handlers.isEmpty() ? null : handlers.get(0);
    }
}
