package com.cqrs.commands;

import com.cqrs.annotations.HandlersMap;
import com.cqrs.annotations.MessageHandler;

public class CommandSubscriberByMap implements CommandSubscriber {

    private final HandlersMap handlersMap;

    public CommandSubscriberByMap(HandlersMap handlersMap) {
        this.handlersMap = handlersMap;
    }

    @Override
    public MessageHandler  getAggregateForCommand(Class<?> commandClass
    ) throws CommandHandlerNotFound {
        MessageHandler  entry = handlersMap.getMap().get(commandClass.getCanonicalName()).get(0);
        if (entry == null) {
            throw new CommandHandlerNotFound(commandClass.getCanonicalName());
        }
        return entry;
    }
}
