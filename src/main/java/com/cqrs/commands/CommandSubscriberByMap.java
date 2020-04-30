package com.cqrs.commands;

public class CommandSubscriberByMap implements CommandSubscriber {

    private final CommandHandlersMap map;

    public CommandSubscriberByMap(CommandHandlersMap map)
    {
        this.map = map;
    }

    @Override
    public CommandHandlerDescriptor getAggregateForCommand(Class<?> commandClass) throws CommandHandlerNotFound
    {
        String[] entry = map.getMap().get(commandClass.getCanonicalName());
        if (entry == null) {
            throw new CommandHandlerNotFound(commandClass.getCanonicalName());
        }
        return new CommandHandlerDescriptor(entry[0], entry[1]);
    }
}
