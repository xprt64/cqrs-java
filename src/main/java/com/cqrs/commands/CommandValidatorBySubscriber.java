package com.cqrs.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class CommandValidatorBySubscriber implements CommandValidator {

    private final CommandValidatorSubscriber validatorSubscriber;

    public CommandValidatorBySubscriber(CommandValidatorSubscriber eventSubscriber) {
        this.validatorSubscriber = eventSubscriber;
    }

    @Override
    public List<Throwable> validateCommand(CommandWithMetadata commandWithMetadata) {
        List<Function<CommandWithMetadata, List<Throwable>>> listeners =
            validatorSubscriber.getValidatorsForCommand(commandWithMetadata.command);
        List<Throwable> errors = new ArrayList<>();
        listeners.forEach(listener -> errors.addAll(listener.apply(commandWithMetadata)));
        return errors;
    }

}


