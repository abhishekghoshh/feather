package io.github.abhishekghoshh.feather.annotation;

public @interface Run {
    String name() default "";

    boolean after() default true;

    boolean bean() default true;
}
