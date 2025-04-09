package io.github.abhishekghoshh.feather.annotation;

/**
 * Annotation to mark a field for dependency injection.
 * <p>
 * This annotation is used to indicate that a field should be injected with a dependency by the Feather framework.
 * The name attribute can be used to specify the name of the bean to be injected.
 * </p>
 *
 * @since 1.0.0
 */
public @interface Inject {
    String name() default "";
}
