package com.cqrs.annotations.processors.command_handler_processor.ok_command_extends;

import com.cqrs.annotations.CommandHandler;
import com.cqrs.annotations.CommandHandlersProcessor;
import com.cqrs.base.Aggregate;
import com.cqrs.base.Command;
import com.cqrs.commands.CommandMetaData;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.Test;

import javax.tools.StandardLocation;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

class CommandHandlersProcessorTest {

    @Test
    void process() throws MalformedURLException {
        assertAbout(javaSource())
            .that(JavaFileObjects.forResource(__FILE__()))
            .processedWith(new CommandHandlersProcessor())
            .compilesWithoutError()
            .and()
            .generatesFileNamed(StandardLocation.SOURCE_OUTPUT, CommandHandlersProcessor.AGGREGATE_COMMAND_HANDLERS_DIRECTORY, MyAggregate.class.getCanonicalName())
            .withStringContents(
                Charset.defaultCharset(),
                "com.cqrs.annotations.processors.command_handler_processor.ok_command_extends.MyCommand,handleComm"
            )
        ;
    }

    private URL __FILE__() throws MalformedURLException {
        File source = new File("src/test/java/" + this.getClass().getPackage().getName().replace('.', '/') + "/" + new Throwable().getStackTrace()[0].getFileName());
        return source.toURI().toURL();
    }
}

class MyAggregate extends Aggregate {

    @CommandHandler
    public void handleComm(MyCommand command) {

    }
}

class MyCommand extends MyAbstractCommand {
}

abstract class MyAbstractCommand implements Command{

    @Override
    public String getAggregateId() {
        return null;
    }
}