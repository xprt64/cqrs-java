package com.cqrs.questions;

import com.cqrs.annotations.HandlersMap.Handler;

public interface AnswererResolver {
    Handler findAnswerer(Object question);
}
