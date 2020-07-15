package com.cqrs.questions;

import com.cqrs.annotations.HandlersMap;
import com.cqrs.annotations.HandlersMap.Handler;

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
    public List<Handler> findSubscribers(Object question) {
        return handlersMap.getMap(question.getClass()).getOrDefault(question.getClass().getCanonicalName(), Collections.emptyList());
    }
}
