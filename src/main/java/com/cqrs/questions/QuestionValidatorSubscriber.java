package com.cqrs.questions;

import com.cqrs.base.Question;

import java.util.List;
import java.util.function.Function;

public interface QuestionValidatorSubscriber {

    List<Function<Question, List<Throwable>>> getValidatorsForQuestion(Question command);
}
