package fr.ariloxe.arena.utils;

import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityTypes;
import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ReflectionUtils {
  public static void registerEntity(String name, int id, Class<? extends Entity> customClass) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
    Method method = EntityTypes.class.getDeclaredMethod("a", new Class[] { Class.class, String.class, int.class });
    method.setAccessible(true);
    method.invoke((Object)null, new Object[] { customClass, name, Integer.valueOf(id) });
  }
  
  public static void unregisterEntity(String name, int id) throws IllegalAccessException {
    List<Map<?, ?>> dataMap = new ArrayList<>();
    for (Field f : EntityTypes.class.getDeclaredFields()) {
      if (f.getType().getSimpleName().equals(Map.class.getSimpleName())) {
        f.setAccessible(true);
        dataMap.add((Map<?, ?>)f.get((Object)null));
      } 
    } 
    if (((Map)dataMap.get(2)).containsKey(Integer.valueOf(id))) {
      ((Map)dataMap.get(0)).remove(name);
      ((Map)dataMap.get(2)).remove(Integer.valueOf(id));
      ((Map)dataMap.get(4)).remove(name);
    } 
  }
  
  public static Constructor<?> getConstructor(Class<?> clazz, Class<?>... parameterTypes) throws NoSuchMethodException {
    Class<?>[] primitiveTypes = DataType.getPrimitive(parameterTypes);
    Constructor[] arrayOfConstructor;
    int i;
    byte b;
    for (arrayOfConstructor = (Constructor[])clazz.getConstructors(), i = arrayOfConstructor.length, b = 0; b < i; ) {
      Constructor<?> constructor = arrayOfConstructor[b];
      if (!DataType.compare(DataType.getPrimitive(constructor.getParameterTypes()), primitiveTypes)) {
        b++;
        continue;
      } 
      return constructor;
    } 
    throw new NoSuchMethodException("There is no such constructor in this class with the specified parameter types");
  }
  
  public static Constructor<?> getConstructor(String className, PackageType packageType, Class<?>... parameterTypes) throws NoSuchMethodException, ClassNotFoundException {
    return getConstructor(packageType.getClass(className), parameterTypes);
  }
  
  public static Object instantiateObject(Class<?> clazz, Object... arguments) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
    return getConstructor(clazz, DataType.getPrimitive(arguments)).newInstance(arguments);
  }
  
  public static Object instantiateObject(String className, PackageType packageType, Object... arguments) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
    return instantiateObject(packageType.getClass(className), arguments);
  }
  
  public static Method getMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
    Class<?>[] primitiveTypes = DataType.getPrimitive(parameterTypes);
    Method[] arrayOfMethod;
    int i;
    byte b;
    for (arrayOfMethod = clazz.getMethods(), i = arrayOfMethod.length, b = 0; b < i; ) {
      Method method = arrayOfMethod[b];
      if (!method.getName().equals(methodName) || 
        !DataType.compare(DataType.getPrimitive(method.getParameterTypes()), primitiveTypes)) {
        b++;
        continue;
      } 
      return method;
    } 
    throw new NoSuchMethodException("There is no such method in this class with the specified name and parameter types");
  }
  
  public static Method getMethod(String className, PackageType packageType, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException, ClassNotFoundException {
    return getMethod(packageType.getClass(className), methodName, parameterTypes);
  }
  
  public static Object invokeMethod(Object instance, String methodName, Object... arguments) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
    return getMethod(instance.getClass(), methodName, DataType.getPrimitive(arguments)).invoke(instance, arguments);
  }
  
  public static Object invokeMethod(Object instance, Class<?> clazz, String methodName, Object... arguments) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
    return getMethod(clazz, methodName, DataType.getPrimitive(arguments)).invoke(instance, arguments);
  }
  
  public static Object invokeMethod(Object instance, String className, PackageType packageType, String methodName, Object... arguments) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
    return invokeMethod(instance, packageType.getClass(className), methodName, arguments);
  }
  
  public static Field getField(Class<?> clazz, boolean declared, String fieldName) throws NoSuchFieldException, SecurityException {
    Field field = declared ? clazz.getDeclaredField(fieldName) : clazz.getField(fieldName);
    field.setAccessible(true);
    return field;
  }
  
  public static Field getField(String className, PackageType packageType, boolean declared, String fieldName) throws NoSuchFieldException, SecurityException, ClassNotFoundException {
    return getField(packageType.getClass(className), declared, fieldName);
  }
  
  public static Object getValue(Object instance, Class<?> clazz, boolean declared, String fieldName) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
    return getField(clazz, declared, fieldName).get(instance);
  }
  
  public static Object getValue(Object instance, String className, PackageType packageType, boolean declared, String fieldName) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException, ClassNotFoundException {
    return getValue(instance, packageType.getClass(className), declared, fieldName);
  }
  
  public static Object getValue(Object instance, boolean declared, String fieldName) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
    return getValue(instance, instance.getClass(), declared, fieldName);
  }
  
  public static void setValue(Object instance, Class<?> clazz, boolean declared, String fieldName, Object value) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
    getField(clazz, declared, fieldName).set(instance, value);
  }
  
  public static void setValue(Object instance, String className, PackageType packageType, boolean declared, String fieldName, Object value) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException, ClassNotFoundException {
    setValue(instance, packageType.getClass(className), declared, fieldName, value);
  }
  
  public static void setValue(Object instance, boolean declared, String fieldName, Object value) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
    setValue(instance, instance.getClass(), declared, fieldName, value);
  }
  
  public static boolean setField(Object toMod, String field, Class<?> claz, Object object) {
    try {
      Field f = claz.getDeclaredField(field);
      f.setAccessible(true);
      f.set(toMod, object);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    } 
  }
  
  public static boolean setField(Object toMod, String field, Object object) {
    try {
      Field f = toMod.getClass().getDeclaredField(field);
      f.setAccessible(true);
      f.set(toMod, object);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    } 
  }
  
  public static Object getField(Object toMod, String field) {
    try {
      Field f = toMod.getClass().getDeclaredField(field);
      f.setAccessible(true);
      return f.get(toMod);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    } 
  }
  
  public static Object getField(Object toMod, String field, Class<?> claz) {
    try {
      Field f = claz.getDeclaredField(field);
      f.setAccessible(true);
      return f.get(toMod);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    } 
  }
  
  public enum PackageType {
    MINECRAFT_SERVER("net.minecraft.server." + getServerVersion()),
    CRAFTBUKKIT("org.bukkit.craftbukkit." + 
      getServerVersion()),
    CRAFTBUKKIT_BLOCK("block"),
    CRAFTBUKKIT_CHUNKIO("chunkio"),
    CRAFTBUKKIT_COMMAND("command"),
    CRAFTBUKKIT_CONVERSATIONS("conversations"),
    CRAFTBUKKIT_ENCHANTMENS("enchantments"),
    CRAFTBUKKIT_ENTITY("entity"),
    CRAFTBUKKIT_EVENT("event"),
    CRAFTBUKKIT_GENERATOR("generator"),
    CRAFTBUKKIT_HELP("help"),
    CRAFTBUKKIT_INVENTORY("inventory"),
    CRAFTBUKKIT_MAP("map"),
    CRAFTBUKKIT_METADATA("metadata"),
    CRAFTBUKKIT_POTION("potion"),
    CRAFTBUKKIT_PROJECTILES("projectiles"),
    CRAFTBUKKIT_SCHEDULER("scheduler"),
    CRAFTBUKKIT_SCOREBOARD("scoreboard"),
    CRAFTBUKKIT_UPDATER("updater"),
    CRAFTBUKKIT_UTIL("util");
    
    private final String path;
    
    PackageType(String path) {
      this.path = path;
    }
    
    public static String getServerVersion() {
      return Bukkit.getServer().getClass().getPackage().getName().substring(23);
    }
    
    public String getPath() {
      return this.path;
    }
    
    public Class<?> getClass(String className) throws ClassNotFoundException {
      return Class.forName(this + "." + className);
    }
    
    public String toString() {
      return this.path;
    }
  }
  
  public enum DataType {
    BYTE(byte.class, Byte.class),
    SHORT(short.class, Short.class),
    INTEGER(int.class, Integer.class),
    LONG(long.class, Long.class),
    CHARACTER(char.class, Character.class),
    FLOAT(float.class, Float.class),
    DOUBLE(double.class, Double.class),
    BOOLEAN(boolean.class, Boolean.class);
    
    private static final Map<Class<?>, DataType> CLASS_MAP = new HashMap<>();
    
    private final Class<?> primitive;
    
    private final Class<?> reference;
    
    static {
      for (DataType type : values()) {
        CLASS_MAP.put(type.primitive, type);
        CLASS_MAP.put(type.reference, type);
      } 
    }
    
    DataType(Class<?> primitive, Class<?> reference) {
      this.primitive = primitive;
      this.reference = reference;
    }
    
    public static DataType fromClass(Class<?> clazz) {
      return CLASS_MAP.get(clazz);
    }
    
    public static Class<?> getPrimitive(Class<?> clazz) {
      DataType type = fromClass(clazz);
      return (type == null) ? clazz : type.getPrimitive();
    }
    
    public static Class<?> getReference(Class<?> clazz) {
      DataType type = fromClass(clazz);
      return (type == null) ? clazz : type.getReference();
    }
    
    public static Class<?>[] getPrimitive(Class<?>[] classes) {
      int length = (classes == null) ? 0 : classes.length;
      Class<?>[] types = new Class[length];
      for (int index = 0; index < length; index++)
        types[index] = getPrimitive(classes[index]); 
      return types;
    }
    
    public static Class<?>[] getReference(Class<?>[] classes) {
      int length = (classes == null) ? 0 : classes.length;
      Class<?>[] types = new Class[length];
      for (int index = 0; index < length; index++)
        types[index] = getReference(classes[index]); 
      return types;
    }
    
    public static Class<?>[] getPrimitive(Object[] objects) {
      int length = (objects == null) ? 0 : objects.length;
      Class<?>[] types = new Class[length];
      for (int index = 0; index < length; index++)
        types[index] = getPrimitive(objects[index].getClass()); 
      return types;
    }
    
    public static Class<?>[] getReference(Object[] objects) {
      int length = (objects == null) ? 0 : objects.length;
      Class<?>[] types = new Class[length];
      for (int index = 0; index < length; index++)
        types[index] = getReference(objects[index].getClass()); 
      return types;
    }
    
    public static boolean compare(Class<?>[] primary, Class<?>[] secondary) {
      if (primary == null || secondary == null || primary.length != secondary.length)
        return false; 
      for (int index = 0; index < primary.length; ) {
        Class<?> primaryClass = primary[index];
        Class<?> secondaryClass = secondary[index];
        if (primaryClass.equals(secondaryClass) || primaryClass.isAssignableFrom(secondaryClass)) {
          index++;
          continue;
        } 
        return false;
      } 
      return true;
    }
    
    public Class<?> getPrimitive() {
      return this.primitive;
    }
    
    public Class<?> getReference() {
      return this.reference;
    }
  }
}
