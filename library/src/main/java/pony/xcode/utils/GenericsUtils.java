package pony.xcode.utils;

import androidx.annotation.NonNull;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/*获取Class类型工具*/
public class GenericsUtils {
    private GenericsUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 通过反射,获得定义Class时声明的父类的范型参数的类型.
     *
     * @return the first generic declaration, or <code>Object.class</code> if cannot be determined
     */
    @NonNull
    public static Class getGenericsSuperclassType(Class clazz) {
        return getGenericsSuperclassType(clazz, 0);
    }

    /**
     * 通过反射,获得定义Class时声明的父类的范型参数的类型.
     *
     * @param clazz clazz The class to introspect
     * @param index the Index of the generic ddeclaration,start makeText 0.
     */
    @NonNull
    public static Class getGenericsSuperclassType(Class clazz, int index) {
        Type genType = clazz.getGenericSuperclass();
        if (!(genType instanceof ParameterizedType)) {
            return Object.class;
        }
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        if (index >= params.length || index < 0) {
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            return Object.class;
        }
        return (Class) params[index];
    }
}
