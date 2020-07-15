package com.cqrs.questions.DefaultAskerTests;

import com.cqrs.annotations.HandlersMap.Handler;
import com.cqrs.infrastructure.AbstractFactory;
import com.cqrs.questions.DefaultAsker;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class DefaultAskerTest {

    public static final String EXPECTED_ANSWER = "a";
    public static final String QUESTION = "q";

    @Test
    void askAndReturn() {
        assertDoesNotThrow(() -> {
            DefaultAsker sut = new DefaultAsker(
                    new MyAbstractFactory(),
                    question -> new Handler(MyAnswerer.class.getCanonicalName(), "answer1"),
                    question -> Collections.emptyList()
            );

            MyQuestion answeredQuestion = sut.askAndReturn(new MyQuestion(QUESTION));

            assertEquals(answeredQuestion.question, QUESTION);
            assertEquals(answeredQuestion.answer, EXPECTED_ANSWER);
       });
    }

    @Test
    void askAndNotifyAsker() {
        assertDoesNotThrow(() -> {
            DefaultAsker sut = new DefaultAsker(
                    new MyAbstractFactory(),
                    question -> new Handler(MyAnswerer.class.getCanonicalName(), "answer1"),
                    question -> Arrays.asList(
                            new Handler("someSubscribedAsker1", "whenAnswered1"),
                            new Handler(MySubscribedAsker.class.getCanonicalName(), "whenAnswered"),
                            new Handler("someSubscribedAsker2", "whenAnswered2")
                    )
            );

            MySubscribedAsker asker = new MySubscribedAsker();
            sut.askAndNotifyAsker(new MyQuestion(QUESTION), asker);

            assertNotNull(asker.answeredQuestion);
            assertEquals(asker.answeredQuestion.question, QUESTION);
            assertEquals(asker.answeredQuestion.answer, EXPECTED_ANSWER);
        });
    }
}

class MyQuestion {
    final public String question;
    public String answer;

    public MyQuestion(String question) {
        this.question = question;
    }

    public MyQuestion(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }
}

class MyAnswerer {
    MyQuestion answer1(MyQuestion q) {
        return new MyQuestion(q.question, DefaultAskerTest.EXPECTED_ANSWER);
    }
}

class MySubscribedAsker {
    public MyQuestion answeredQuestion;

    void whenAnswered(MyQuestion q) {
        answeredQuestion = q;
    }
}

class MyAbstractFactory implements AbstractFactory {

    @Override
    public Object factory(Class<?> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }
}