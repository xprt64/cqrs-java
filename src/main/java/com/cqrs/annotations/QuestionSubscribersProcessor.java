package com.cqrs.annotations;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;
import java.util.*;
import java.util.stream.Collectors;

@SupportedAnnotationTypes("com.cqrs.annotations.QuestionSubscriber")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class QuestionSubscribersProcessor extends AbstractProcessor {
    public static final String QUESTION_SUBSCRIBERS_DIRECTORY = "com_cqrs_annotations_questionSubscribers";

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

    private String getOutputDirectory(){
        return QUESTION_SUBSCRIBERS_DIRECTORY;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        annotations.forEach(annotation -> {
            try {
                Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
                writeCode(getQuestionSubscribers(annotatedElements, annotation));
            } catch (Exception e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "" + e.getMessage());
            }
        });

        return false;
    }

    protected void writeCode(HashMap<String, List<HandlerDescriptor>> byListener) throws IOException {

        byListener.forEach((listenerClass, eventHandlers) -> {
            try {
                System.out.println("Question subscribers for " + listenerClass);
                System.out.println(
                    "    Write to " + StandardLocation.SOURCE_OUTPUT + "/" + getOutputDirectory() + "/" +
                    listenerClass);
                final Writer writer = processingEnv.getFiler()
                    .createResource(StandardLocation.SOURCE_OUTPUT, getOutputDirectory(), listenerClass)
                    .openWriter();
                try {
                    writer.write(
                        eventHandlers.stream()
                            .map(queryHandler -> queryHandler.parameterClass + "," + queryHandler.methodName)
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

    private HashMap<String, List<HandlerDescriptor>> getQuestionSubscribers(
        Set<? extends Element> annotatedElements,
        TypeElement annotation
    ) throws Exception {
        HashMap<String, List<HandlerDescriptor>> handlers = new HashMap<>();
        /*
         * @QuestionSubscriber
         * - to non-static methods
         * - arguments: Question
         * - return: void (or anything but is ignored)
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
                    "Question subscriber must have only one parameter, the query",
                    element,
                    getAnnotationMirror(element, annotation)
                ));
            } else {
                TypeMirror firstParam = type.getParameterTypes().get(0);
                TypeElement firstParamElement =
                    processingEnv.getElementUtils().getTypeElement(firstParam.toString());
                queryClassName = firstParamElement.getQualifiedName().toString();

                if (firstParam.getKind().isPrimitive()) {
                    errors.add(new Error(
                        "First parameter of a query handler must be a class",
                        firstParamElement
                    ));
                }
            }
            if (errors.size() > 0) {
                errors.forEach(this::error);
                throw new Exception();
            } else {
                List<HandlerDescriptor> existing = handlers.getOrDefault(listenerClassName, new LinkedList<>());
                existing.add(new HandlerDescriptor(queryClassName, methodName));
                handlers.put(listenerClassName, existing);
            }
        }
        return handlers;
    }
}
