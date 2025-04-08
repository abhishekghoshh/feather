package io.github.abhishekghoshh.feather;

import java.util.List;

public class Feather {
    // after creating the object, it will be locked
    private static Context INSTANCE = null;


    public static synchronized Context build(Class<?> className) throws Exception {
        List<Class<?>> classes = ClassUtil.allClasses(className.getPackageName());
        return new Context();
    }
}
