package com.cqrs.util;

import java.io.*;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

public class ResourceReader {

    private static InputStream getResourceAsStream(String resource) {
        final InputStream in = getContextClassLoader().getResourceAsStream(resource);
        return in == null ? ResourceReader.class.getResourceAsStream(resource) : in;
    }

    private static ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    public void forEachLineInDirectory(String dirPath, BiConsumer<String, String> consumer, BiConsumer<String, IOException> errorReporter) {
        getResourceFiles(dirPath).forEach(file -> {
            try {
                final String fileName = Objects.requireNonNull(getContextClassLoader().getResource(Paths.get(dirPath, file).toString())).getFile();
                try (FileReader reader = new FileReader(new File(fileName))) {
                    new BufferedReader(reader).lines().forEach(line -> {
                        consumer.accept(file, line);
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
                if (errorReporter != null) {
                    errorReporter.accept(file, e);
                }
            }
        });
    }

    public void forEachLineInDirectory(String dirPath, BiConsumer<String, String> consumer) {
        forEachLineInDirectory(dirPath, consumer, null);
    }

    private Stream<String> getResourceFiles(String path) {
        final InputStream resource = getResourceAsStream(path);
        if (null == resource) {
            throw new NullPointerException("Could not find resource directory " + path);
        }
        return new BufferedReader(new InputStreamReader(resource)).lines();
    }
}
