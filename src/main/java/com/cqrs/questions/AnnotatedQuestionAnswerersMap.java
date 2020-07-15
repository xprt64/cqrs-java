package com.cqrs.questions;

import com.cqrs.annotations.HandlersMapFromFile;
import com.cqrs.annotations.QuestionAnswerersProcessor;

public class AnnotatedQuestionAnswerersMap extends HandlersMapFromFile {

    public AnnotatedQuestionAnswerersMap() {
        super(QuestionAnswerersProcessor.QUESTION_ANSWERERS_DIRECTORY);
    }
}
