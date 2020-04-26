package com.dudulina.command;

public interface CommandSubscriber {
    public CommandHandlerDescriptor getAggregateForCommand(Class<?> commandClass) throws CommandHandlerNotFound;
}
