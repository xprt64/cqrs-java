package com.cqrs.annotations.processors.once_event_handler_processor.ok1;

import com.cqrs.annotations.OnceEventHandler;
import com.cqrs.annotations.OnceEventHandlersProcessor;
import com.cqrs.base.Event;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static javax.tools.StandardLocation.SOURCE_OUTPUT;

class OnceEventHandlersProcessorTest {

    @Test
    void process() throws MalformedURLException {
        assertAbout(javaSource())
            .that(JavaFileObjects.forResource(__FILE__()))
            .processedWith(new OnceEventHandlersProcessor())
            .compilesWithoutError()
            .and()
            .generatesFileNamed(
                SOURCE_OUTPUT,
                OnceEventHandlersProcessor.EVENT_HANDLERS_DIRECTORY,
                MyEventListener.class.getCanonicalName()
            )
            .withStringContents(
                Charset.defaultCharset(),
                "com.cqrs.annotations.processors.once_event_handler_processor.ok1.OnceEventHandlersProcessorTest.MyEvent1,handleEvent1"
            )
        ;
    }

    private URL __FILE__() throws MalformedURLException {
        File source = new File("src/test/java/" + this.getClass().getPackage().getName().replace('.', '/') + "/" + new Throwable().getStackTrace()[0].getFileName());
        return source.toURI().toURL();
    }


    class MyEventListener {

        @OnceEventHandler
        public void handleEvent1(MyEvent1 event) {

        }
    }

    class MyEvent1 implements Event {

    }
}
