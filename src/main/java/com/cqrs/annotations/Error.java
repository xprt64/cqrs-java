package com.cqrs.annotations;


import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;

class Error {

    public final String error;
    public final Element element;
    public final AnnotationMirror annotationMirror;

    public Error(String error, Element element) {
        this.error = error;
        this.element = element;
        this.annotationMirror = null;
    }

    public Error(String error, Element element, AnnotationMirror annotationMirror) {
        this.error = error;
        this.element = element;
        this.annotationMirror = annotationMirror;
    }
}