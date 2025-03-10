package io.github.abhishekghoshh.feather;

public class Feather {
    // after creating the object, it will be locked
    private static Context INSTANCE = null;


    public static synchronized Context build(Class<?> clazz) {
        return new Context();
    }
}
