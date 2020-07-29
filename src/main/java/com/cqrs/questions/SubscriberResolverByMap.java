package com.cqrs.questions;

import com.cqrs.annotations.HandlersMap;
import com.cqrs.annotations.MessageHandler;

import java.util.Collections;
import java.util.List;

public class SubscriberResolverByMap implements SubscriberResolver {
    private final HandlersMap handlersMap;

    public SubscriberResolverByMap(
            HandlersMap handlersMap
    ) {
        this.handlersMap = handlersMap;
    }

    @Override
    public List<MessageHandler> findSubscribers(Object question) {
        return handlersMap.getMap().getOrDefault(question.getClass().getCanonicalName(), Collections.emptyList());
    }
}
