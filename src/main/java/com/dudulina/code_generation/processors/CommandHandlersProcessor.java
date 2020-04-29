package com.dudulina.code_generation.processors;

import com.dudulina.base.Aggregate;
import com.dudulina.base.Command;
import com.dudulina.command.CommandMetaData;
import com.google.auto.service.AutoService;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

@SupportedAnnotationTypes("com.dudulina.code_generation.annotations.CommandHandler")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
//@AutoService(Processor.class)
public class CommandHandlersProcessor extends AbstractProcessor {

    public static final String builderClassName = "CommandHandlersMapImpl";
    public static final String packageName = "com.dudulina.code_generation";

    private static AnnotationMirror getAnnotationMirror(Element element, TypeElement annotation) {
        for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
            if (annotationMirror.getAnnotationType().toString().equals(annotation.getQualifiedName().toString())) {
                return annotationMirror;
            }
        }
        return null;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            try {
                writeCode(getCommandHandlers(roundEnv.getElementsAnnotatedWith(annotation), annotation));
            } catch (Exception e) {
                return false;
            }
        }

        return false;
    }

    private HashMap<String, CommandHandler> getCommandHandlers(Set<? extends Element> annotatedElements, TypeElement annotation) throws Exception {
        HashMap<String, CommandHandler> handlers = new HashMap<>();
        final Messager messager = processingEnv.getMessager();

        /*
         * @CommandHandler
         * - to non-static methods
         * - into Aggregate class
         * - arguments: Command and optional CommandMeta
         */
        for (Element element : annotatedElements) {
            ExecutableType type = (ExecutableType) element.asType();
            DeclaredType enclosingType = (DeclaredType) element.getEnclosingElement().asType();
            String aggregateClassName = enclosingType.toString();

            String commandClassName = "";
            ArrayList<Error> errors = new ArrayList<>();

            final String methodName = element.getSimpleName().toString();
            if (element.getKind() != ElementKind.METHOD) {
                errors.add(new Error(
                        "annotated element is not a method (only methods can be annotated with @CommandHandler)",
                        element,
                        getAnnotationMirror(element, annotation)
                ));
                throw new Exception();
            }

            if (element.getModifiers().contains(Modifier.STATIC)) {
                errors.add(new Error(
                        "method is static (only non-static methods can be annotated with @CommandHandler)",
                        element,
                        getAnnotationMirror(element, annotation)
                ));
            }

            final TypeElement aggregateType = processingEnv.getElementUtils().getTypeElement(aggregateClassName);
            if (!aggregateType.getSuperclass().toString().equals(Aggregate.class.getCanonicalName())) {
                errors.add(new Error(
                        "Class does not extent an " + Aggregate.class.getCanonicalName()
                                + " (only Aggregates can handle commands)",
                        element.getEnclosingElement()
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

                TypeElement firstParamElement = processingEnv.getElementUtils().getTypeElement(firstParam.toString());
                commandClassName = firstParamElement.getQualifiedName().toString();

                if (firstParam.getKind().isPrimitive() || !typeIsInstanceOfInterface(firstParamElement, Command.class.getCanonicalName())) {
                    errors.add(new Error(
                            "First parameter must be instance of " + Command.class.getCanonicalName(),
                            firstParamElement
                    ));
                } else {
                    if (type.getParameterTypes().size() > 1) {
                        TypeMirror secondParam = type.getParameterTypes().get(0);
                        TypeElement secondParamElement = processingEnv.getElementUtils().getTypeElement(type.getParameterTypes().get(1).toString());
                        if (secondParam.getKind().isPrimitive() || !secondParamElement.getQualifiedName().toString()
                                .equals(CommandMetaData.class.getCanonicalName())) {
                            errors.add(new Error(
                                    "Second optional parameter must be instance of " + CommandMetaData.class.getCanonicalName(),
                                    secondParamElement
                            ));
                        }
                    }
                    if (handlers.containsKey(commandClassName)) {
                        CommandHandler existing = handlers.get(commandClassName);
                        errors.add(new Error(
                                "Only one command handler per command is permitted; this command " + commandClassName + " has " + existing.aggregateClass
                                        + "::" + existing.methodName + " and also " + aggregateClassName + "::" + methodName.toString(),
                                element,
                                getAnnotationMirror(element, annotation)
                        ));
                    }
                }
            }
            if (errors.size() > 0) {
                errors.forEach(this::error);
                throw new Exception();
            } else {
                handlers.put(commandClassName, new CommandHandler(aggregateClassName, methodName.toString()));
            }
        }
        return handlers;
    }

    private void writeCode(HashMap<String, CommandHandler> handlers) throws IOException {
        if (handlers.isEmpty()) {
            return;
        }

        JavaFileObject builderFile = processingEnv.getFiler()
                .createSourceFile(packageName + "." + builderClassName);
        try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {

            out.print("package ");
            out.print(packageName);
            out.println(";");
            out.println();

            out.print("import java.util.HashMap;");
            out.println();
            out.print("public class " + builderClassName + " implements com.dudulina.command.CommandHandlersMap ");
            out.println(" {");
            out.println();

            out.println(" @Override");
            out.println(" public HashMap<String, String[]> getMap() { return map;} ");
            out.println(" private static HashMap<String, String[]> map = new HashMap<>(); ");
            out.println(" static { ");
            handlers.forEach((s, commandHandler) -> out
                    .println("map.put(\"" + s + "\", new String[]{\"" + commandHandler.aggregateClass + "\",\"" + commandHandler.methodName + "\"});"));
            out.println(" } ");
            out.println();

            out.println("}");
        }
    }

    private boolean typeIsInstanceOfInterface(TypeElement type, String base) {
        return type.getInterfaces().stream().map(TypeMirror::toString).collect(Collectors.toList()).contains(base);
    }

    private void error(Error error) {
        if (error.annotationMirror != null) {
            processingEnv.getMessager().printMessage(Kind.ERROR, error.error, error.element, error.annotationMirror);
        } else {
            processingEnv.getMessager().printMessage(Kind.ERROR, error.error, error.element);
        }
    }
}

class CommandHandler {

    public final String aggregateClass;
    public final String methodName;

    public CommandHandler(String aggregateClass, String methodName) {
        this.aggregateClass = aggregateClass;
        this.methodName = methodName;
    }
}