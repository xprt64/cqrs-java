package com.cqrs.annotations;

import com.cqrs.base.Event;
import com.cqrs.events.MetaData;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;
import java.util.*;
import java.util.stream.Collectors;

abstract public class AbstractEventHandlerProcessor extends AbstractProcessor {
    protected static AnnotationMirror getAnnotationMirror(Element element, TypeElement annotation) {
        for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
            if (annotationMirror.getAnnotationType()
                .toString()
                .equals(annotation.getQualifiedName().toString())) {
                return annotationMirror;
            }
        }
        return null;
    }

    protected abstract String getOutputDirectory();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        annotations.forEach(annotation -> {
            try {
                Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
                writeCode(getEventHandlers(annotatedElements, annotation));
            } catch (Exception e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "" + e.getMessage());
            }
        });

        return false;
    }

    protected void writeCode(HashMap<String, List<EventHandler>> byListener) throws IOException {

        byListener.forEach((listenerClass, eventHandlers) -> {
            try {
                System.out.println("Event listeners for " + listenerClass);
                System.out.println(
                    "Write to " + StandardLocation.SOURCE_OUTPUT + "/" + getOutputDirectory() + "/" +
                    listenerClass);
                final Writer writer = processingEnv.getFiler()
                    .createResource(StandardLocation.SOURCE_OUTPUT, getOutputDirectory(), listenerClass)
                    .openWriter();
                try {
                    writer.write(
                        eventHandlers.stream()
                            .map(eventHandler -> eventHandler.eventClass + "," + eventHandler.methodName)
                            .collect(Collectors.joining("\n"))
                    );
                } catch (IOException e) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
                }

                writer.flush();
                writer.close();
            } catch (IOException e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
            }
        });

    }

    protected boolean typeIsInstanceOfInterface(TypeElement type, String base) {
        return type.getInterfaces()
            .stream()
            .map(TypeMirror::toString)
            .collect(Collectors.toList())
            .contains(base);
    }

    protected void error(Error error) {
        if (error.annotationMirror != null) {
            processingEnv.getMessager()
                .printMessage(Diagnostic.Kind.ERROR, error.error, error.element, error.annotationMirror);
        } else {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, error.error, error.element);
        }
    }

    private HashMap<String, List<EventHandler>> getEventHandlers(
        Set<? extends Element> annotatedElements,
        TypeElement annotation
    ) throws Exception {
        HashMap<String, List<EventHandler>> handlers = new HashMap<>();
        /*
         * @EventHandler
         * - to non-static methods
         * - arguments: Event and optional EventMeta
         */
        for (Element element : annotatedElements) {
            ExecutableType type = (ExecutableType) element.asType();
            DeclaredType enclosingType = (DeclaredType) element.getEnclosingElement().asType();
            String listenerClassName = enclosingType.toString();

            String eventClassName = "";
            ArrayList<Error> errors = new ArrayList<>();

            final String methodName = element.getSimpleName().toString();
            if (element.getKind() != ElementKind.METHOD) {
                processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.ERROR,
                    "only methods can be annotated with @" + annotation.getQualifiedName() + "",
                    element,
                    getAnnotationMirror(element, annotation)
                );
                throw new Exception();
            }

            if (element.getModifiers().contains(Modifier.STATIC)) {
                errors.add(new com.cqrs.annotations.Error(
                    "only non-static methods can be annotated with @" + annotation.getQualifiedName() + "",
                    element,
                    getAnnotationMirror(element, annotation)
                ));
            }

            if (type.getParameterTypes().size() < 1) {
                errors.add(new com.cqrs.annotations.Error(
                    "Event handler must have at least the Event parameter as the first parameter",
                    element,
                    getAnnotationMirror(element, annotation)
                ));
            } else {
                TypeMirror firstParam = type.getParameterTypes().get(0);
                TypeElement firstParamElement =
                    processingEnv.getElementUtils().getTypeElement(firstParam.toString());
                eventClassName = firstParamElement.getQualifiedName().toString();

                if (firstParam.getKind().isPrimitive() ||
                    !typeIsInstanceOfInterface(firstParamElement, Event.class.getCanonicalName())) {
                    errors.add(new com.cqrs.annotations.Error(
                        "First parameter must be instance of " + Event.class.getCanonicalName(),
                        firstParamElement
                    ));
                } else {
                    if (type.getParameterTypes().size() > 1) {
                        TypeMirror secondParam = type.getParameterTypes().get(0);
                        TypeElement secondParamElement = processingEnv.getElementUtils()
                            .getTypeElement(type.getParameterTypes().get(1).toString());
                        if (secondParam.getKind().isPrimitive() ||
                            !secondParamElement.getQualifiedName().toString()
                                .equals(MetaData.class.getCanonicalName())) {
                            errors.add(new com.cqrs.annotations.Error(
                                "Second optional parameter must be instance of " +
                                MetaData.class.getCanonicalName(),
                                secondParamElement
                            ));
                        }
                    }
                }
            }
            if (errors.size() > 0) {
                errors.forEach(this::error);
                throw new Exception();
            } else {
                List<EventHandler> existing = handlers.getOrDefault(listenerClassName, new LinkedList<>());
                existing.add(new EventHandler(eventClassName, methodName));
                handlers.put(listenerClassName, existing);
            }
        }
        return handlers;
    }

    static class EventHandler {

        public final String eventClass;
        public final String methodName;

        public EventHandler(String eventClass, String methodName) {
            this.eventClass = eventClass;
            this.methodName = methodName;
        }
    }
}
