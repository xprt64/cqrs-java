package com.cqrs.commands;

public interface CommandSubscriber {
    CommandHandlerDescriptor getAggregateForCommand(Class<?> commandClass) throws CommandHandlerNotFound;
}
