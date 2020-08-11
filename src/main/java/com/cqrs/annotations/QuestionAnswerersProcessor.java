package com.cqrs.annotations;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

@SupportedAnnotationTypes("com.cqrs.annotations.QuestionAnswerer")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class QuestionAnswerersProcessor extends AbstractProcessor {
    public static final String QUESTION_ANSWERERS_DIRECTORY = "com_cqrs_annotations_questionAnswerers";

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

    private String getOutputDirectory() {
        return QUESTION_ANSWERERS_DIRECTORY;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        annotations.forEach(annotation -> {
            try {
                Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
                writeCode(getQuestionHandlers(annotatedElements, annotation));
            } catch (Exception e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "" + e.getMessage());
            }
        });

        return false;
    }

    protected void writeCode(HashMap<String, QuestionHandler> handlers) {
        HashMap<String, List<String>> byAnswerer = new HashMap<>();

        handlers.forEach((command, value) -> {
            List<String> aggregateHandlers = byAnswerer.getOrDefault(value.answererClass, new LinkedList<>());
            aggregateHandlers.add(command + "," + value.methodName);
            byAnswerer.put(value.answererClass, aggregateHandlers);
        });

        byAnswerer.forEach((key, value) -> {
            try {
                System.out.println("Question answerers for " + key);
                System.out.println("    Write to " + StandardLocation.SOURCE_OUTPUT + "/" + getOutputDirectory() + "/" + key);
                final Writer writer = processingEnv.getFiler()
                        .createResource(StandardLocation.SOURCE_OUTPUT, getOutputDirectory(), key)
                        .openWriter();
                Collections.sort(value);
                String commandAndHandler = String.join("\n", value);
                writer.write(commandAndHandler);
                writer.flush();
                writer.close();
                System.out.println("    " + commandAndHandler);
            } catch (IOException e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
            }
        });
    }

    protected boolean typeExtendsSuperClass(TypeElement type, String base) {
        return type.getSuperclass().toString().equals(base);
    }

    protected void error(Error error) {
        if (error.annotationMirror != null) {
            processingEnv.getMessager()
                    .printMessage(Diagnostic.Kind.ERROR, error.error, error.element, error.annotationMirror);
        } else {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, error.error, error.element);
        }
    }

    private HashMap<String, QuestionHandler> getQuestionHandlers(
            Set<? extends Element> annotatedElements,
            TypeElement annotation
    ) throws Exception {
        HashMap<String, QuestionHandler> handlers = new HashMap<>();
        /*
         * @EventHandler
         * - to non-static methods
         * - arguments: Event and optional EventMeta
         */
        for (Element element : annotatedElements) {
            ExecutableType type = (ExecutableType) element.asType();
            DeclaredType enclosingType = (DeclaredType) element.getEnclosingElement().asType();
            String listenerClassName = enclosingType.toString();

            String queryClassName = "";
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
                errors.add(new Error(
                        "only non-static methods can be annotated with @" + annotation.getQualifiedName() + "",
                        element,
                        getAnnotationMirror(element, annotation)
                ));
            }

            if (type.getParameterTypes().size() != 1) {
                errors.add(new Error(
                        "Question handler must have only one parameter, the query",
                        element,
                        getAnnotationMirror(element, annotation)
                ));
            } else {
                TypeMirror firstParam = type.getParameterTypes().get(0);
                TypeElement firstParamElement =
                        processingEnv.getElementUtils().getTypeElement(firstParam.toString());
                queryClassName = firstParam.toString();

                if (firstParam.getKind().isPrimitive() ||
                        !typeExtendsSuperClass(firstParamElement, Object.class.getCanonicalName())) {
                    errors.add(new Error(
                            "First parameter of a query handler must extend " + Object.class.getCanonicalName(),
                            firstParamElement
                    ));
                }

                if (!type.getReturnType().toString().equals(queryClassName)) {
                    errors.add(new Error(
                            "Return type of a query handler must be instance of " + queryClassName,
                            processingEnv.getElementUtils().getTypeElement(type.getReturnType().toString())
                    ));
                }
                if (handlers.containsKey(queryClassName)) {
                    QuestionHandler existing = handlers.get(queryClassName);
                    errors.add(new Error(
                            "Only one question answerer per question is permitted; this question " +
                                    queryClassName + " has " + existing.answererClass
                                    + "::" + existing.methodName + " and also " + listenerClassName + "::" +
                                    methodName,
                            element,
                            getAnnotationMirror(element, annotation)
                    ));
                }
            }
            if (errors.size() > 0) {
                errors.forEach(this::error);
                throw new Exception();
            } else {
                handlers.put(queryClassName, new QuestionHandler(listenerClassName, methodName));
            }
        }
        return handlers;
    }

    static class QuestionHandler {

        public final String answererClass;
        public final String methodName;

        public QuestionHandler(String answererClass, String methodName) {
            this.answererClass = answererClass;
            this.methodName = methodName;
        }
    }
}
