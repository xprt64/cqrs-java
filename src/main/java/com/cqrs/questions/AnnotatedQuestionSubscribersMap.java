package com.cqrs.questions;

import com.cqrs.annotations.HandlersMapFromFile;
import com.cqrs.annotations.QuestionAnswerersProcessor;
import com.cqrs.annotations.QuestionSubscribersProcessor;

public class AnnotatedQuestionSubscribersMap extends HandlersMapFromFile {

    public AnnotatedQuestionSubscribersMap() {
        super(QuestionSubscribersProcessor.QUESTION_SUBSCRIBERS_DIRECTORY);
    }
}
