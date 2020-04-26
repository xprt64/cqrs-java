package com.dudulina.code_generation.processors;

import com.dudulina.base.Command;
import com.dudulina.command.CommandMetaData;
import com.google.auto.service.AutoService;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic.Kind;

@SupportedAnnotationTypes("com.dudulina.code_generation.annotations.CommandHandler")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class CommandHandlersProcessor extends AbstractProcessor
{

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
    {
        for (TypeElement annotation : annotations) {
            Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
            HashMap<String, CommandHandler> handlers = new HashMap<>();

            /*
             * @CommandHandler
             * - to methods
             * - arguments: Command and optional CommandMeta
             */
            for (Element element : annotatedElements) {
                if (element.getKind() != ElementKind.METHOD) {
                    error("Only methods can be annotated with @CommandHandler");
                    return false;
                }
                if (element.getEnclosingElement().getKind() != ElementKind.CLASS) {
                    error("Only methods from classes can be annotated with @CommandHandler");
                    return false;
                }

                ExecutableType type = (ExecutableType) element.asType();
                DeclaredType enclosingType = (DeclaredType) element.getEnclosingElement().asType();
                String aggregateClassName = enclosingType.toString();

                if (type.getParameterTypes().size() < 1) {
                    error("CommandHandler must have at least the Command parameter as the first parameter");
                    return false;
                }
                TypeMirror firstParam = type.getParameterTypes().get(0);

                TypeElement firstParamElement = processingEnv.getElementUtils().getTypeElement(firstParam.toString());
                final String commandClassName = firstParamElement.getQualifiedName().toString();

                if (firstParam.getKind().isPrimitive() || !typeIsInstanceOfInterface(firstParamElement, Command.class.getCanonicalName())) {
                    error("First parameter " + aggregateClassName + "::" + element.getSimpleName() + " " + type.toString() + " must be instance of " + Command.class
                        .getCanonicalName());
                    return false;
                }
                if (type.getParameterTypes().size() > 1) {
                    TypeMirror secondParam = type.getParameterTypes().get(0);
                    TypeElement secondParamElement = processingEnv.getElementUtils().getTypeElement(type.getParameterTypes().get(1).toString());
                    if (secondParam.getKind().isPrimitive() || !secondParamElement.getQualifiedName().toString().equals(CommandMetaData.class.getCanonicalName())) {
                        error("Second parameter of " + type.toString() + " must be instance of " + CommandMetaData.class.getCanonicalName());
                        return false;
                    }
                }
                if (handlers.containsKey(commandClassName)) {
                    CommandHandler existing = handlers.get(commandClassName);
                    error("Only one CommandHandler per command is permitted; this command " + commandClassName + " has " + existing.aggregateClass
                        + "::" + existing.methodName + " and also " + aggregateClassName + "::" + element.getSimpleName().toString());
                    return false;
                }
                handlers.put(commandClassName, new CommandHandler(aggregateClassName, element.getSimpleName().toString()));
            }

            System.out.println(handlers);

            // …
        }

        return true;
    }

    private boolean typeIsInstanceOfInterface(TypeElement type,String base){
        return type.getInterfaces().stream().map(TypeMirror::toString).collect(Collectors.toList()).contains(base);
    }

    private void checkElement(Element element)
    {

    }

    private void error(String message)
    {
        processingEnv.getMessager().printMessage(Kind.ERROR, message);
    }
}

class CommandHandler
{
    public final String aggregateClass;
    public final String methodName;

    public CommandHandler(String aggregateClass, String methodName)
    {
        this.aggregateClass = aggregateClass;
        this.methodName = methodName;
    }
}