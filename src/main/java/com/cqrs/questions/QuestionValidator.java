package com.cqrs.questions;

import com.cqrs.base.Question;

import java.util.List;

public interface QuestionValidator {

    List<Throwable> validateQuestion(Question question);
}
