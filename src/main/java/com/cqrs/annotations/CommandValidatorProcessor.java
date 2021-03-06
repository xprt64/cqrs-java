package com.cqrs.annotations;

import com.cqrs.base.Command;
import com.cqrs.commands.CommandMetaData;

import javax.annotation.processing.*;
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

@SupportedAnnotationTypes("com.cqrs.annotations.CommandValidator")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class CommandValidatorProcessor extends AbstractProcessor {

    public static final String COMMAND_VALIDATORS_DIRECTORY = "com_cqrs_annotations_commandValidators";

    private static AnnotationMirror getAnnotationMirror(Element element, TypeElement annotation) {
        for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
            if (annotationMirror.getAnnotationType()
                .toString()
                .equals(annotation.getQualifiedName().toString())) {
                return annotationMirror;
            }
        }
        return null;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        annotations.forEach(annotation -> {
            try {
                writeCode(getCommandValidators(roundEnv.getElementsAnnotatedWith(annotation), annotation));
            } catch (Exception e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "" + e.getMessage());
            }
        });
        return false;
    }

    private HashMap<String, List<CommandValidator>> getCommandValidators(
        Set<? extends Element> annotatedElements,
        TypeElement annotation
    ) throws Exception {
        HashMap<String, List<CommandValidator>> handlers = new HashMap<>();
        final Messager messager = processingEnv.getMessager();
        /*
         * @CommandValidator
         * - to non-static methods
         * - returns Throwable or List<Throwable> or throws something
         * - arguments: Command and optional CommandMeta
         */
        for (Element element : annotatedElements) {
            ExecutableType type = (ExecutableType) element.asType();
            DeclaredType enclosingType = (DeclaredType) element.getEnclosingElement().asType();
            String listenerClassName = enclosingType.toString();

            String commandClassName = "";
            ArrayList<Error> errors = new ArrayList<>();

            final String methodName = element.getSimpleName().toString();
            final int order = element.getAnnotation(com.cqrs.annotations.CommandValidator.class).order();
            if (element.getKind() != ElementKind.METHOD) {
                messager.printMessage(
                    Diagnostic.Kind.ERROR,
                    "annotated element is not a method (only methods can be annotated with @CommandValidator)",
                    element,
                    getAnnotationMirror(element, annotation)
                );
                throw new Exception();
            }

            TypeMirror returned = type.getReturnType();

            if (returned.getKind() == TypeKind.VOID && type.getThrownTypes().size() == 0) {
                errors.add(new Error(
                    "validator method must return a Throwable, List<Throwable> or throw a Throwable",
                    element,
                    getAnnotationMirror(element, annotation)
                ));
            }
            if (returned.getKind() == TypeKind.DECLARED) {
                Element returnedElement = ((DeclaredType) returned).asElement();
                List<? extends TypeMirror> templateElements = ((DeclaredType) returned).getTypeArguments();
                if (templateElements.size() > 0) {
                    if (templateElements.size() != 1) {
                        errors.add(new Error(
                            "returned (if template) must be instance of List<Throwable>",
                            element,
                            getAnnotationMirror(element, annotation)
                        ));
                    } else {
                        TypeMirror firstTemplate = templateElements.get(0);
                        if (firstTemplate.getKind() != TypeKind.DECLARED) {
                            errors.add(new Error(
                                "returned (if template) must be instance of List<Throwable>",
                                element,
                                getAnnotationMirror(element, annotation)
                            ));
                        }
                    }
                } else {
                    if (!returned.toString().equals(Throwable.class.getCanonicalName()) &&
                        !typeIsInstanceOfInterface(
                            processingEnv.getElementUtils().getTypeElement(returned.toString()),
                            Throwable.class.getCanonicalName()
                        )
                    ) {
                        errors.add(new Error(
                            "returned (if not a template) must be a Throwable",
                            element,
                            getAnnotationMirror(element, annotation)
                        ));
                    }
                }
            }

            if (element.getModifiers().contains(Modifier.STATIC)) {
                errors.add(new Error(
                    "method is static (only non-static methods can be annotated with @CommandValidator)",
                    element,
                    getAnnotationMirror(element, annotation)
                ));
            }

            if (type.getParameterTypes().size() < 1) {
                errors.add(new Error(
                    "Command handler must have at least the Command parameter as the first parameter",
                    element,
                    getAnnotationMirror(element, annotation)
                ));
            } else {
                TypeMirror firstParam = type.getParameterTypes().get(0);

                TypeElement firstParamElement =
                    processingEnv.getElementUtils().getTypeElement(firstParam.toString());
                commandClassName = firstParamElement.getQualifiedName().toString();

                if (firstParam.getKind().isPrimitive() ||
                    !typeIsInstanceOfInterface(firstParamElement, Command.class.getCanonicalName())) {
                    errors.add(new Error(
                        "First parameter must be instance of " + Command.class.getCanonicalName(),
                        firstParamElement
                    ));
                } else {
                    if (type.getParameterTypes().size() > 1) {
                        TypeMirror secondParam = type.getParameterTypes().get(0);
                        TypeElement secondParamElement = processingEnv.getElementUtils()
                            .getTypeElement(type.getParameterTypes().get(1).toString());
                        if (secondParam.getKind().isPrimitive() ||
                            !secondParamElement.getQualifiedName().toString()
                                .equals(CommandMetaData.class.getCanonicalName())) {
                            errors.add(new Error(
                                "Second optional parameter must be instance of " +
                                CommandMetaData.class.getCanonicalName(),
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
                List<CommandValidator> existing =
                    handlers.getOrDefault(listenerClassName, new LinkedList<>());
                existing.add(new CommandValidator(commandClassName, methodName, order));
                handlers.put(listenerClassName, existing);
            }
        }
        return handlers;
    }


    protected void writeCode(HashMap<String, List<CommandValidator>> byListener) throws IOException {

        byListener.forEach((listenerClass, eventHandlers) -> {
            try {
                System.out.println("Command validators in " + listenerClass);
                System.out.println(
                    "Write to " + StandardLocation.SOURCE_OUTPUT + "/" + COMMAND_VALIDATORS_DIRECTORY + "/" +
                    listenerClass);
                final Writer writer = processingEnv.getFiler()
                    .createResource(
                        StandardLocation.SOURCE_OUTPUT,
                        COMMAND_VALIDATORS_DIRECTORY,
                        listenerClass
                    )
                    .openWriter();
                try {
                    writer.write(
                        eventHandlers.stream()
                            .map(eventHandler -> eventHandler.commandClassName + "," + eventHandler.methodName + "," + eventHandler.order)
                            .sorted()
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

    private boolean typeIsInstanceOfInterface(TypeElement type, String base) {
        return this.processingEnv.getTypeUtils().isAssignable(type.asType(), this.processingEnv.getElementUtils().getTypeElement(base).asType());
   }

    private void error(Error error) {
        if (error.annotationMirror != null) {
            processingEnv.getMessager()
                .printMessage(Diagnostic.Kind.ERROR, error.error, error.element, error.annotationMirror);
        } else {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, error.error, error.element);
        }
    }

    static class CommandValidator {

        public final String commandClassName;
        public final String methodName;
        public final int order;

        public CommandValidator(String commandClassName, String methodName, int order) {
            this.commandClassName = commandClassName;
            this.methodName = methodName;
            this.order = order;
        }
    }
}
