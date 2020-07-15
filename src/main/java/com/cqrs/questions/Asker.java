package com.cqrs.questions;

import com.cqrs.questions.exceptions.HandlerException;

public interface Asker {
    <Q> Q askAndReturn(Q question) throws HandlerException;
    <Q> void askAndNotifyAsker(Q question, Object asker) throws HandlerException;
}
