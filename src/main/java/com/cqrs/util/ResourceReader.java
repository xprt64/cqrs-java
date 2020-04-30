package com.cqrs.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class ResourceReader {

    public static void forEachLineInResource(String file, Consumer<String> consumer) throws IOException {
        forEachLine(
            new File(Objects.requireNonNull(getContextClassLoader().getResource(file)).getFile()),
            consumer
        );
    }

    public static void forEachLine(File file, Consumer<String> consumer) throws IOException {

        if (file == null) return;

        try (FileReader reader = new FileReader(file);
             BufferedReader br = new BufferedReader(reader)) {

            String line;
            while ((line = br.readLine()) != null) {
                consumer.accept(line);
            }
        }
    }


    public static List<String> getResourceFiles(String path) throws IOException {
        List<String> filenames = new ArrayList<>();
        try (
            InputStream in = getResourceAsStream(path);
            BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            String resource;

            while ((resource = br.readLine()) != null) {
                filenames.add(resource);
            }
        }

        return filenames;
    }

    private static InputStream getResourceAsStream(String resource) {
        final InputStream in = getContextClassLoader().getResourceAsStream(resource);
        return in == null ? ResourceReader.class.getResourceAsStream(resource) : in;
    }

    private static ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }
}
