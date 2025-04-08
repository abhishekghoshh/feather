package io.github.abhishekghoshh.feather.annotation;

public @interface Bean {
    String name() default "";

    boolean singleton() default true;

    boolean primary() default false;

}
