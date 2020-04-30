package com.cqrs.commands;

import com.cqrs.base.Command;
import java.util.List;

public interface CommandValidator {

    List<Throwable> validateCommand(Command command);
}
