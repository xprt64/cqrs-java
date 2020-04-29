package com.dudulina.code_generation.processors.command_handler_processor.ok1;

import com.dudulina.base.Aggregate;
import com.dudulina.base.Command;
import com.dudulina.code_generation.annotations.CommandHandler;
import com.dudulina.code_generation.processors.CommandHandlersProcessor;
import com.dudulina.command.CommandMetaData;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.Test;

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
                .generatesFileNamed(javax.tools.StandardLocation.SOURCE_OUTPUT, CommandHandlersProcessor.packageName, CommandHandlersProcessor.builderClassName + ".java")
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
