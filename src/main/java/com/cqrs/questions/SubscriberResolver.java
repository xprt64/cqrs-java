package com.cqrs.questions;

import com.cqrs.annotations.MessageHandler;

import java.util.List;

public interface SubscriberResolver {
    List<MessageHandler> findSubscribers(Object question);
}
