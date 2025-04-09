package io.github.abhishekghoshh.feather.annotation;

public @interface Run {
    String name() default "";

    boolean after() default false;

    boolean bean() default true;
}
