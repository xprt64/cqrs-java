package com.cqrs.commands;

import com.cqrs.exceptions_base.MessageRejectedByValidators;

import java.util.List;
import java.util.stream.Collectors;

public class CommandRejectedByValidators extends MessageRejectedByValidators {
    public CommandRejectedByValidators(List<Throwable> errors) {
        super(errors);
    }
}
