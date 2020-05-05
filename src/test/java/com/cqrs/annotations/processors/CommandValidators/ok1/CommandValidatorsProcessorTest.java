package com.cqrs.annotations.processors.CommandValidators.ok1;

import com.cqrs.annotations.*;
import com.cqrs.base.Command;
import com.cqrs.base.Event;
import com.cqrs.events.MetaData;
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

class CommandValidatorsProcessorTest {

    @Test
    void process() throws MalformedURLException {
        assertAbout(javaSource())
            .that(JavaFileObjects.forResource(__FILE__()))
            .processedWith(new CommandValidatorProcessor())
            .compilesWithoutError()
            .and()
            .generatesFileNamed(
                SOURCE_OUTPUT,
                CommandValidatorProcessor.COMMAND_VALIDATORS_DIRECTORY,
                MyCommandValidator.class.getCanonicalName()
            )
            .withStringContents(
                Charset.defaultCharset(),
                "com.cqrs.annotations.processors.CommandValidators.ok1.CommandValidatorsProcessorTest.MyCommand1,validate1\n" +
                "com.cqrs.annotations.processors.CommandValidators.ok1.CommandValidatorsProcessorTest.MyCommand2,validate2\n" +
                "com.cqrs.annotations.processors.CommandValidators.ok1.CommandValidatorsProcessorTest.MyCommand3,validate3"
            )
        ;
    }

    private URL __FILE__() throws MalformedURLException {
        File source = new File("src/test/java/" + this.getClass().getPackage().getName().replace('.', '/') + "/" + new Throwable().getStackTrace()[0].getFileName());
        return source.toURI().toURL();
    }

    class MyCommandValidator {

        @CommandValidator
        public List<Throwable> validate1(MyCommand1 command) {
            return new ArrayList<>();
        }

        @CommandValidator
        public void validate2(MyCommand2 command) throws Exception {

        }

        @CommandValidator
        public Throwable validate3(MyCommand3 command) {
            return new Exception("");
        }

    }

    class MyCommand1 implements Command{

        @Override
        public String getAggregateId() {
            return null;
        }
    }

    class MyCommand2 implements Command{

        @Override
        public String getAggregateId() {
            return null;
        }
    }

    class MyCommand3 implements Command{

        @Override
        public String getAggregateId() {
            return null;
        }
    }
}
