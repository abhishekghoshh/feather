package io.github.abhishekghoshh.feather.annotation;

public @interface EntryPoint {
    String[] packages() default {};
}
