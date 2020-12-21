package com.cqrs.annotations.processors.QuestionValidators.ordering;

import com.cqrs.annotations.QuestionValidator;
import com.cqrs.annotations.QuestionValidatorProcessor;
import com.cqrs.base.Question;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static javax.tools.StandardLocation.SOURCE_OUTPUT;

class QuestionValidatorsProcessorTest {

    @Test
    void process() throws MalformedURLException {
        assertAbout(javaSource())
            .that(JavaFileObjects.forResource(__FILE__()))
            .processedWith(new QuestionValidatorProcessor())
            .compilesWithoutError()
            .and()
            .generatesFileNamed(
                SOURCE_OUTPUT,
                QuestionValidatorProcessor.QUESTION_VALIDATORS_DIRECTORY,
                MyQuestionValidator.class.getCanonicalName()
            )
            .withStringContents(
                Charset.defaultCharset(),
                "" +
                "com.cqrs.annotations.processors.QuestionValidators.ordering.MyQuestion1,validate1,1\n" +
                "com.cqrs.annotations.processors.QuestionValidators.ordering.MyQuestion2,validate2,0\n" +
                "com.cqrs.annotations.processors.QuestionValidators.ordering.MyQuestion3,validate3,3\n" +
                "com.cqrs.annotations.processors.QuestionValidators.ordering.MyQuestion3,validate5,5\n" +
                "com.cqrs.annotations.processors.QuestionValidators.ordering.MyQuestion3,validate7,7"
            )
        ;
    }

    private URL __FILE__() throws MalformedURLException {
        File source = new File("src/test/java/" + this.getClass().getPackage().getName().replace('.', '/') + "/" + new Throwable().getStackTrace()[0].getFileName());
        return source.toURI().toURL();
    }


}
class MyQuestionValidator {

    @QuestionValidator(order = 1)
    public void validate1(MyQuestion1 command) throws Exception  {

    }

    @QuestionValidator
    public void validate2(MyQuestion2 command) throws Exception {

    }

    @QuestionValidator(order=3)
    public void validate3(MyQuestion3 command) throws Exception  {

    }

    @QuestionValidator(order=7)
    public void validate7(MyQuestion3 command) throws Exception  {

    }

    @QuestionValidator(order=5)
    public void validate5(MyQuestion3 command) throws Exception  {

    }

}

class MyQuestion1 implements Question{
}

class MyQuestion2 implements Question{
}

class MyQuestion3 implements Question{
}
