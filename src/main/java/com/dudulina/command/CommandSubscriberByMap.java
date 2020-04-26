package com.dudulina.command;

import java.util.HashMap;

public class CommandSubscriberByMap implements CommandSubscriber {

    private final HashMap<String, String[]> map;

    public CommandSubscriberByMap(HashMap<String, String[]> map)
    {
        this.map = map;
    }

    @Override
    public CommandHandlerDescriptor getAggregateForCommand(Class<?> commandClass) throws CommandHandlerNotFound
    {
        String[] entry = map.get(commandClass.getCanonicalName());
        if (entry == null) {
            throw new CommandHandlerNotFound(commandClass.getCanonicalName());
        }
        return new CommandHandlerDescriptor(entry[0], entry[1]);
    }
}
