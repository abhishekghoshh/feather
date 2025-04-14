package io.github.abhishekghoshh.feather.annotation;

public @interface Run {
    String name() default "";

    Class<?> type() default Object.class;
}
