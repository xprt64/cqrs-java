package com.cqrs.questions.DefaultQuestionPublisherTests.ok1;

import com.cqrs.annotations.MessageHandler;
import com.cqrs.questions.DefaultQuestionPublisher;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class DefaultQuestionPublisherTest {
    Throwable exception;

    @Test
    void publishAnsweredQuestion() {
        exception = null;
        MySubscriber.reset();

        DefaultQuestionPublisher sut = new DefaultQuestionPublisher(
                question -> Collections.singletonList(
                        new MessageHandler(MySubscriber.class.getCanonicalName(), "whenAnswered")
                ),
                clazz -> {
                    try {
                        return clazz.getDeclaredConstructor().newInstance();
                    } catch (Throwable e) {
                        fail();
                    }
                    return null;
                },
                (listenerInstance, listenerClass, methodName, question, throwable) -> exception = throwable,
                getClass().getClassLoader()
        );

        String question = new String("q1");
        sut.publishAnsweredQuestion(question);

        assertNull(exception);

        assertEquals("q1", MySubscriber.publishedQuestion);
        assertEquals(1, MySubscriber.cntPublished);
    }


}
class MySubscriber {
    static String publishedQuestion;
    static int cntPublished;

    static void reset() {
        publishedQuestion = null;
        cntPublished = 0;
    }

    public void whenAnswered(String question) {
        publishedQuestion = question;
        cntPublished++;
    }
}
