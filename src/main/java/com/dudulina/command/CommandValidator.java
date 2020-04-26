package com.dudulina.command;

import com.dudulina.base.Command;
import java.util.List;

public interface CommandValidator {

    List<Throwable> validateCommand(Command command);
}
