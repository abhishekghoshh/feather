package io.github.abhishekghoshh.feather.annotation;

public @interface Bean {
    // if there is a name then it will be used as bean name
    // if there are two beans with the same name, then it will throw an exception
    String name() default "";

    // if there is a type, then it will be used as the bean type and Class.getCanonicalName() will be used as bean name
    // if there are two beans with the same type then which bean is primary
    // if type is not defined then it will use the Class.getCanonicalName() as bean type
    Class<?> type() default Object.class;

    // if types is defined then it will register for bean for all types and it will use
    Class<?>[] types() default {};

    // if lazy is true, then the bean will be created only when it is necessary,
    // and it will not be injected to any class
    // if lazy = true then application has to ask Feather.getLazyBean(name)
    // or Feather.getLazyBean(Class<?> type) to get the bean
    // if there are more than one lazy beans of the same type then it will throw an exception
    boolean lazy() default false;

    // there shall not be two beans with primary = true of the same type
    boolean primary() default false;


}
