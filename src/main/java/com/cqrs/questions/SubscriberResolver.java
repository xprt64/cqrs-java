package com.cqrs.questions;

import com.cqrs.annotations.HandlersMap.Handler;

import java.util.List;

public interface SubscriberResolver {
    List<Handler> findSubscribers(Object question);
}
