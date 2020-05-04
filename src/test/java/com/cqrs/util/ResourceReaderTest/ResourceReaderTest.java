package com.cqrs.util.ResourceReaderTest;

import com.cqrs.util.ResourceReader;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ResourceReaderTest {

    @Test
    void forEachLineInDirectory() {
        ResourceReader sut = new ResourceReader();
        StringBuilder stringBuilder = new StringBuilder();

        sut.forEachLineInDirectory(this.getClass(), "ResourceReaderTestDir", (file, line) -> {
            stringBuilder.append(file).append(":").append(line).append("\n");
        });

        assertEquals("a.txt:line1\na.txt:line2\nb.txt:line3\nb.txt:line4\n", stringBuilder.toString());
    }
}