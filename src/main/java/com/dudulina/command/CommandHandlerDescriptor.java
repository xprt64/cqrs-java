package com.dudulina.command;

public class CommandHandlerDescriptor {

    public final String aggregateClass;
    public final String methodName;
    
	public CommandHandlerDescriptor(String aggregateClass, String methodName) {
		this.aggregateClass = aggregateClass;
		this.methodName = methodName;
	}  

}
