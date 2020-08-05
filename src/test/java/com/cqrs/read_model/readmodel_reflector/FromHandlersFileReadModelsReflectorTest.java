package com.cqrs.read_model.readmodel_reflector;

import com.cqrs.util.ResourceReader;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FromHandlersFileReadModelsReflectorTest {

    @Test
    void getEventClassesFromReadModel() {
        FromHandlersFileReadModelsReflector sut = new FromHandlersFileReadModelsReflector(
            new ResourceReader(this.getClass()),
            "FromHandlersFileReadModelsReflectorTest"
        );

        List<String> actualClasses = sut.getEventClassesFromReadModel("ReadModel1");

        assertLinesMatch(actualClasses, Arrays.asList("event1", "event2"));

        assertEquals("handler1", sut.getEventHandlerMethodNameForEvent("ReadModel1","event1"));
    }
}
