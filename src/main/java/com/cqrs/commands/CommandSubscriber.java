package com.cqrs.commands;

import com.cqrs.annotations.MessageHandler;

public interface CommandSubscriber {
    MessageHandler  getAggregateForCommand(Class<?> commandClass) throws CommandHandlerNotFound;
}
