package com.cqrs.questions;

import com.cqrs.base.Question;
import com.cqrs.questions.exceptions.HandlerException;

public interface Asker {
    <Q extends Question> Q askAndReturn(Q question) throws HandlerException;
    <Q extends Question> void askAndNotifyAsker(Q question, Object asker) throws HandlerException;
}
