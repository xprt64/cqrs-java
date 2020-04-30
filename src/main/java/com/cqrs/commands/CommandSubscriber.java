package com.cqrs.commands;

public interface CommandSubscriber {
    public CommandHandlerDescriptor getAggregateForCommand(Class<?> commandClass) throws CommandHandlerNotFound;
}
