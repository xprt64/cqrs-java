package com.dudulina.code_generation.processors;

import static org.junit.jupiter.api.Assertions.*;

import com.dudulina.aggregates.AggregateId;
import com.dudulina.base.Aggregate;
import com.dudulina.base.Command;
import com.dudulina.code_generation.annotations.CommandHandler;
import com.dudulina.command.CommandMetaData;
import com.google.testing.compile.JavaFileObjects;
import java.io.File;
import java.net.MalformedURLException;
import org.junit.jupiter.api.Test;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static com.google.common.truth.Truth.assertAbout;

class CommandHandlersProcessorTest
{

    @Test
    void process() throws MalformedURLException
    {
        File source = new File("src/test/java/com/dudulina/code_generation/processors/CommandHandlersProcessorTest.java");

        assertAbout(javaSource())
              .that(JavaFileObjects.forResource(source.toURI().toURL()))
              .processedWith(new CommandHandlersProcessor())
              .compilesWithoutError()
              .and().generatesSources(JavaFileObjects.forResource("GeneratedHelloWorld.java"));
    }
}

class MyAggregate extends Aggregate{

    @CommandHandler
    public void handleComm(MyCommand command){

    }

    @CommandHandler
    public void handleComm2(MyCommand2 command, CommandMetaData meta){

    }
}

class MyCommand implements Command{

    @Override
    public AggregateId getAggregateId()
    {
        return null;
    }
}

class MyCommand2 implements Command{

    @Override
    public AggregateId getAggregateId()
    {
        return null;
    }
}