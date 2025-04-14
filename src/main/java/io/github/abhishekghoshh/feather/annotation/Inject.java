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
    /**
     * Specifies the name of the bean to be injected.
     * If not specified, the field's type will be used to resolve the bean.
     *
     * @return the name of the bean
     */
    String name() default "";

    /**
     * Specifies the type of the dependency to be injected.
     * If not specified, the type will be inferred from the field's type.
     *
     * @return the class type of the dependency
     */
    Class<?> type() default Object.class;

    /**
     * Indicates whether the dependency is required.
     * If set to true, an exception will be thrown if the dependency cannot be injected.
     * If set to false, the field will be set to null if the dependency cannot be injected.
     *
     * @return true if the dependency is required, false otherwise
     */
    boolean required() default true;
}
