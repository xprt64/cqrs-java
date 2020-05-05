package com.cqrs.commands;

import java.util.List;

public interface CommandValidator {

    List<Throwable> validateCommand(CommandWithMetadata commandWithMetadata);
}
