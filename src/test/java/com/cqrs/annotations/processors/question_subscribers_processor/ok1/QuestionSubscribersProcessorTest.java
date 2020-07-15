package com.cqrs.annotations.processors.question_subscribers_processor.ok1;

import com.cqrs.annotations.QuestionSubscriber;
import com.cqrs.annotations.QuestionSubscribersProcessor;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static javax.tools.StandardLocation.SOURCE_OUTPUT;

class QuestionSubscribersProcessorTest {

    @Test
    void process() throws MalformedURLException {
        assertAbout(javaSource())
            .that(JavaFileObjects.forResource(__FILE__()))
            .processedWith(new QuestionSubscribersProcessor())
            .compilesWithoutError()
            .and()
            .generatesFileNamed(
                SOURCE_OUTPUT,
                    QuestionSubscribersProcessor.QUESTION_SUBSCRIBERS_DIRECTORY,
                MyQuestionHandler.class.getCanonicalName()
            )
            .withStringContents(
                Charset.defaultCharset(),
                "com.cqrs.annotations.processors.question_subscribers_processor.ok1.QuestionSubscribersProcessorTest.MyQuestion1,whenAnswered1\n" +
                "com.cqrs.annotations.processors.question_subscribers_processor.ok1.QuestionSubscribersProcessorTest.MyQuestion2,whenAnswered2"
            )
        ;
    }

    private URL __FILE__() throws MalformedURLException {
        File source = new File("src/test/java/" + this.getClass().getPackage().getName().replace('.', '/') + "/" + new Throwable().getStackTrace()[0].getFileName());
        return source.toURI().toURL();
    }

    static class MyQuestionHandler {

        @QuestionSubscriber
        public void whenAnswered1(MyQuestion1 query) {

        }

        @QuestionSubscriber
        public void whenAnswered2(MyQuestion2 query) {

        }
    }

    static class MyQuestion1 {

    }

    static class MyQuestion2  {

    }
}
