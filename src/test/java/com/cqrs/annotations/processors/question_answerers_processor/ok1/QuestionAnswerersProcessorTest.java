package com.cqrs.annotations.processors.question_answerers_processor.ok1;

import com.cqrs.annotations.QuestionAnswerer;
import com.cqrs.annotations.QuestionAnswerersProcessor;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static javax.tools.StandardLocation.SOURCE_OUTPUT;

class QuestionAnswerersProcessorTest {

    @Test
    void process() throws MalformedURLException {
        assertAbout(javaSource())
                .that(JavaFileObjects.forResource(__FILE__()))
                .processedWith(new QuestionAnswerersProcessor())
                .compilesWithoutError()
                .and()
                .generatesFileNamed(
                        SOURCE_OUTPUT,
                        QuestionAnswerersProcessor.QUESTION_ANSWERERS_DIRECTORY,
                        MyQuestionHandler.class.getCanonicalName()
                )
                .withStringContents(
                        Charset.defaultCharset(),
                        "com.cqrs.annotations.processors.question_answerers_processor.ok1.QuestionAnswerersProcessorTest.MyQuestion1,answerQuestion1\n" +
                                "com.cqrs.annotations.processors.question_answerers_processor.ok1.QuestionAnswerersProcessorTest.MyQuestion2,answerQuestion2"
                )
        ;
    }

    private URL __FILE__() throws MalformedURLException {
        File source = new File("src/test/java/" + this.getClass().getPackage().getName().replace('.', '/') + "/" + new Throwable().getStackTrace()[0].getFileName());
        return source.toURI().toURL();
    }

    static class MyQuestionHandler {

        @QuestionAnswerer
        public MyQuestion1 answerQuestion1(MyQuestion1 query) {
            return query;
        }

        @QuestionAnswerer
        public MyQuestion2 answerQuestion2(MyQuestion2 query) {
            return query;
        }
    }

    static class MyQuestion1 {

    }

    static class MyQuestion2 {

    }
}
