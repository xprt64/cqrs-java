package com.cqrs.commands;

import com.cqrs.base.Command;
import com.cqrs.base.Event;
import com.cqrs.events.MetaData;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public interface CommandValidatorSubscriber {

    List<Function<CommandWithMetadata, List<Throwable>>> getValidatorsForCommand(Command command);
}
