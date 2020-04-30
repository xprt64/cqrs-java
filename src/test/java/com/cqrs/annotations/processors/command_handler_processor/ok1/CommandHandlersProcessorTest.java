package com.cqrs.annotations.processors.command_handler_processor.ok1;

import com.cqrs.base.Aggregate;
import com.cqrs.base.Command;
import com.cqrs.annotations.CommandHandler;
import com.cqrs.annotations.CommandHandlersProcessor;
import com.cqrs.commands.CommandMetaData;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.Test;

import javax.tools.StandardLocation;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

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

    @CommandHandler
    public void handleComm2(MyCommand2 command, CommandMetaData meta) {

    }
}

class MyCommand implements Command {

    @Override
    public String getAggregateId() {
        return null;
    }
}

class MyCommand2 implements Command {

    @Override
    public String getAggregateId() {
        return null;
    }
}
