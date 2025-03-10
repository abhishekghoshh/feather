package io.github.abhishekghoshh.feather.annotation;

public @interface Scan {
    String[] packages() default {};
}
