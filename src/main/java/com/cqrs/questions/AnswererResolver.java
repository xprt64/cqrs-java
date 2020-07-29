package com.cqrs.questions;

import com.cqrs.annotations.MessageHandler;

public interface AnswererResolver {
    MessageHandler findAnswerer(Object question);
}
