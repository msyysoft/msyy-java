package io.github.msyysoft.java.utiltools;

import java.beans.Introspector;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

/**
 * 反射相关的工具类
 */
public class ReflectUtil {

	/** an empty class array */
	public static final Class<?>[] NO_PARAMETERS = new Class<?>[0];

	/** an empty object array */
	public static final Object[] NO_ARGUMENTS = new Object[0];

	/** an empty object array */
	public static final Type[] NO_TYPES = new Type[0];

	public static final String METHOD_GET_PREFIX = "get";
	public static final String METHOD_IS_PREFIX = "is";
	public static final String METHOD_SET_PREFIX = "set";

	// ---------------------------------------------------------------- method0
	private static Method _getMethod0;

	static {
		try {
			_getMethod0 = Class.class.getDeclaredMethod("getMethod0", String.class, Class[].class);
			_getMethod0.setAccessible(true);
		} catch (Exception ignore) {
			try {
				_getMethod0 = Class.class.getMethod("getMethod", String.class, Class[].class);
			} catch (Exception ignored) {
				_getMethod0 =  null;
			}
		}
	}

	/**
	 * 调用<code>Class.getMethod0()</code>私有方法，获取类中的方法。不会抛出<code>NoSuchMethodException</code>异常。
	 * 返回指定的public方法或者没找到返回<code>null</code>。由于没有异常抛出，所以性能良好。
	 *
	 * @param c      			需要处理的Class
	 * @param name   			需要查找的方法名字
	 * @param parameterTypes	需要查找方法的参数类型
	 * @return 查找到的方法或者null
	 */
	public static Method getMethod0(Class<?> c, String name, Class<?>... parameterTypes) {
		try {
			return (Method) _getMethod0.invoke(c, name, parameterTypes);
		} catch (Exception ignore) {
			return null;
		}
	}

	// ---------------------------------------------------------------- find method

	/**
	 * 根据给定的方法名字，从public方法中查找。这个方法会一个一个查找，因此性能不好。返回为第一个匹配可访问的方法。
	 *
	 * @param c          需要获取方法的类
	 * @param methodName 方法的全名
	 * @return 如果找不到返回null，找到返回该方法
	 */
	public static Method findMethod(Class<?> c, String methodName) {
		return findDeclaredMethod(c, methodName, true);
	}
	/**
	 * 根据给定的方法名字，从所有方法中查找。这个方法会一个一个查找，因此性能不好。返回为第一个匹配可访问的方法。
	 *
	 * @param c          需要获取方法的类
	 * @param methodName 方法的全名
	 * @return 如果找不到返回null，找到返回该方法
	 */
	public static Method findDeclaredMethod(Class<?> c, String methodName) {
		return findDeclaredMethod(c, methodName, false);
	}
	/**
	 * 根据给定的方法名字，从public方法或者所有方法中查找。这个方法会一个一个查找，因此性能不好。返回为第一个匹配可访问的方法。
	 *
	 * @param c          需要获取方法的类
	 * @param methodName 方法的全名
	 * @param publicOnly 是否在public范文内查找，还是在所有方法范围内查找
	 * @return 如果找不到返回null，找到返回该方法
	 */
	private static Method findDeclaredMethod(Class<?> c, String methodName, boolean publicOnly) {
		if ((methodName == null) || (c == null)) {
			return null;
		}
		Method[] ms = publicOnly ? c.getMethods() : c.getDeclaredMethods();
		for (Method m : ms) {
			if (m.getName().equals(methodName)) {
				return m;
			}
		}
		return null;
	}


	// ---------------------------------------------------------------- classes

	/**
	 * 返回对象的Class数组。
	 * 
	 * @param objects，需要获取Class的对象
	 * @return Class[] 将各个对象的Class对象装入数组并返回
	 */
	public static Class<?>[] getClasses(Object... objects) {
		if (objects == null) {
			return null;
		}
		Class<?>[] result = new Class<?>[objects.length];
		for (int i = 0; i < objects.length; i++) {
			if (objects[i] != null) {
				result[i] = objects[i].getClass();
			}
		}
		return result;
	}


	// ---------------------------------------------------------------- invoke


	/**
	 * 调用对象中的可访问方法
	 *
	 * @param c      		含有方法的Class
	 * @param obj    		需要执行方法的对象
	 * @param method 		需要调用的方法
	 * @param paramClasses	参数类型Class数组
	 * @param params 		参数列表
	 */
	public static Object invoke(Class<?> c, Object obj, String method, Class<?>[] paramClasses, Object[] params) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		Method m = c.getMethod(method, paramClasses);
		return m.invoke(obj, params);
	}


	/**
	 * 调用对象中的可访问方法
	 *
	 * @param obj    		需要执行方法的对象
	 * @param method 		需要调用的方法
	 * @param paramClasses	参数类型Class数组
	 * @param params 		参数列表
	 */
	public static Object invoke(Object obj, String method, Class<?>[] paramClasses, Object[] params) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		Method m = obj.getClass().getMethod(method, paramClasses);
		return m.invoke(obj, params);
	}

	/**
	 * 调用对象中指定的方法，不需要给定参数类型
	 * @param obj    需要执行方法的对象
	 * @param method 需要调用的方法
	 * @param params 参数列表
	 */
	public static Object invoke(Object obj, String method, Object[] params) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		Class<?>[] paramClass = getClasses(params);
		return invoke(obj, method, paramClass, params);
	}
	/**
	 * 调用对象中指定的方法，不需要给定参数类型
	 * 
	 * @param c      对象的类型Class
	 * @param obj    需要执行方法的对象
	 * @param method 需要调用的方法
	 * @param params 参数列表
	 */
	public static Object invoke(Class<?> c, Object obj, String method, Object[] params) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		Class<?>[] paramClass = getClasses(params);
		return invoke(c, obj, method, paramClass, params);
	}


	// ---------------------------------------------------------------- invokeDeclared


	/**
	 * 调用对象中的任意方法，包括private方法
	 *
	 * @param c      		对象的类型Class
	 * @param obj    		需要执行方法的对象
	 * @param method 		需要调用的方法
	 * @param paramClasses	方法的参数类型
	 * @param params 		方法的参数列表
	 */
	public static Object invokeDeclared(Class<?> c, Object obj, String method, Class<?>[] paramClasses, Object[] params) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		Method m = c.getDeclaredMethod(method, paramClasses);
		m.setAccessible(true);
		return m.invoke(obj, params);
	}

	/**
	 * 忽略Java访问控制，调用类中的任意方法
	 *
	 * @param obj    		需要执行方法的对象
	 * @param method 		需要调用的方法
	 * @param paramClasses	方法的参数类型
	 * @param params 		方法的参数列表
	 */
	public static Object invokeDeclared(Object obj, String method, Class<?>[] paramClasses, Object[] params) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		Method m = obj.getClass().getDeclaredMethod(method, paramClasses);
		m.setAccessible(true);
		return m.invoke(obj, params);
	}
	/**
	 * 忽略Java访问控制，调用类中的任意方法
	 *
	 * @param obj    		需要执行方法的对象
	 * @param method 		需要调用的方法
	 * @param params 		方法的参数列表
	 */
	public static Object invokeDeclared(Object obj, String method, Object[] params) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		Class<?>[] paramClass = getClasses(params);
		return invokeDeclared(obj, method, paramClass, params);
	}
	/**
	 * 忽略Java访问控制，调用类中的任意方法
	 *
	 * @param c      		对象的类型Class
	 * @param obj    		需要执行方法的对象
	 * @param method 		需要调用的方法
	 * @param params 		方法的参数列表
	 */
	public static Object invokeDeclared(Class<?> c, Object obj, String method, Object[] params) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		Class<?>[] paramClass = getClasses(params);
		return invokeDeclared(c, obj, method, paramClass, params);
	}


	// ---------------------------------------------------------------- match classes

	/**
	 * 判断第一个参数Class是否是第二个参数的子类或者接口的实现类。相当于使用<code>instanceof</code>进行判断。
	 * 方法不具有对称性
	 * 
	 * @param thisClass 需要检查的类
	 * @param target 判断thisClass是否instanceof这个target类
	 */
	public static boolean isSubclass(Class<?> thisClass, Class<?> target) {
		if (target.isInterface() != false) {
			return isInterfaceImpl(thisClass, target);
		}
		for (Class<?> x = thisClass; x != null; x = x.getSuperclass()) {
			if (x == target) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 如果thisClass是targetInterface接口的实现类，返回<code>true</code>
	 */
	public static boolean isInterfaceImpl(Class<?> thisClass, Class<?> targetInterface) {
		for (Class<?> x = thisClass; x != null; x = x.getSuperclass()) {
			Class<?>[] interfaces = x.getInterfaces();
			for (Class<?> i : interfaces) {
				if (i == targetInterface) {
					return true;
				}
				if (isInterfaceImpl(i, targetInterface)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * <code>instanceof</code>的动态实现
	 *
	 * @param o			需要检查的对象
	 * @param target	检查的类
	 * @return			如果对象是target类的实现，返回<code>true</code>
	 */
	public static boolean isInstanceOf(Object o, Class<?> target) {
		return isSubclass(o.getClass(), target);
	}

	// ---------------------------------------------------------------- accessible methods


	/**
	 * 返回给定类的所有方法数组
	 * @see #getAccessibleMethods(Class, Class)
	 */
	public static Method[] getAccessibleMethods(Class<?> clazz) {
		return getAccessibleMethods(clazz, Object.class);
	}

	/**
	 * 返回给定类的所有可以访问的方法，直到<code>Object.class</code>类。抽象方法被忽略
	 */
	public static Method[] getAccessibleMethods(Class<?> clazz, Class<?> limit) {
		Package topPackage = clazz.getPackage();
		List<Method> methodList = new ArrayList<Method>();
		int topPackageHash = topPackage == null ? 0 : topPackage.hashCode();
		boolean top = true;
		do {
			if (clazz == null) {
				break;
			}
			Method[] declaredMethods = clazz.getDeclaredMethods();
			for (Method method : declaredMethods) {
				if (Modifier.isVolatile(method.getModifiers())) {
				    continue;
				}
//				if (Modifier.isAbstract(method.getModifiers())) {
//					continue;
//				}
				if (top == true) {				// add all top declared methods
					methodList.add(method);
					continue;
				}
				int modifier = method.getModifiers();
				if (Modifier.isPrivate(modifier) == true) {
					continue;										// ignore super private methods
				}
				if (Modifier.isAbstract(modifier) == true) {		// ignore super abstract methods
					continue;
				}
				if (Modifier.isPublic(modifier) == true) {
					addMethodIfNotExist(methodList, method);		// add super public methods
					continue;
				}
				if (Modifier.isProtected(modifier) == true) {
					addMethodIfNotExist(methodList, method);		// add super protected methods
					continue;
				}
				// add super default methods from the same package
				Package pckg = method.getDeclaringClass().getPackage();
				int pckgHash = pckg == null ? 0 : pckg.hashCode();
				if (pckgHash == topPackageHash) {
					addMethodIfNotExist(methodList, method);
				}
			}
			top = false;
		} while ((clazz = clazz.getSuperclass()) != limit);

		Method[] methods = new Method[methodList.size()];
		for (int i = 0; i < methods.length; i++) {
			methods[i] = methodList.get(i);
		}
		return methods;
	}

	private static void addMethodIfNotExist(List<Method> allMethods, Method newMethod) {
		for (Method m : allMethods) {
			if (compareSignatures(m, newMethod) == true) {
				return;
			}
		}
		allMethods.add(newMethod);
	}

	// ---------------------------------------------------------------- accessible fields

	/**
	 * 返回给定类的所有可以访问的属性，直到<code>Object.class</code>
	 */
	public static Field[] getAccessibleFields(Class<?> clazz) {
		return getAccessibleFields(clazz, Object.class);
	}
	/**
	 * 返回给定类的所有可以访问的属性，直到给定的超类limit
	 * 
	 * @param clazz，当前类
	 * @param limit，限制到的超类
	 * @return 返回从clazz开始到limit所有可以访问的成员变量
	 */
	public static Field[] getAccessibleFields(Class<?> clazz, Class<?> limit) {
		Package topPackage = clazz.getPackage();
		List<Field> fieldList = new ArrayList<Field>();
		int topPackageHash = topPackage == null ? 0 : topPackage.hashCode();
		boolean top = true;
		do {
			if (clazz == null) {
				break;
			}
			Field[] declaredFields = clazz.getDeclaredFields();
			for (Field field : declaredFields) {
				if (top == true) {				// add all top declared fields
					fieldList.add(field);
					continue;
				}
				int modifier = field.getModifiers();
				if (Modifier.isPrivate(modifier) == true) {
					continue;										// ignore super private fields
				}
				if (Modifier.isPublic(modifier) == true) {
					addFieldIfNotExist(fieldList, field);			// add super public methods
					continue;
				}
				if (Modifier.isProtected(modifier) == true) {
					addFieldIfNotExist(fieldList, field);			// add super protected methods
					continue;
				}
				// add super default methods from the same package
				Package pckg = field.getDeclaringClass().getPackage();
				int pckgHash = pckg == null ? 0 : pckg.hashCode();
				if (pckgHash == topPackageHash) {
					addFieldIfNotExist(fieldList, field);
				}
			}
			top = false;
		} while ((clazz = clazz.getSuperclass()) != limit);

		Field[] fields = new Field[fieldList.size()];
		for (int i = 0; i < fields.length; i++) {
			fields[i] = fieldList.get(i);
		}
		return fields;
	}

	private static void addFieldIfNotExist(List<Field> allFields, Field newField) {
		for (Field f : allFields) {
			if (compareSignatures(f, newField) == true) {
				return;
			}
		}
		allFields.add(newField);
	}


	// ---------------------------------------------------------------- supported methods

	/**
	 * 返回给定类的所有的方法，直到<code>Object.class</code>
	 * 
	 * @param clazz，当前类
	 * @return 从clazz到Object所有的方法
	 */
	public static Method[] getSupportedMethods(Class<?> clazz) {
		return getSupportedMethods(clazz, Object.class);
	}

	/**
	 * 返回从clazz到超类limit所有的方法数组。使用limit主要是限制不返回<code>java.lang.Object</code>中的方法，如果是<code>null</code>，那么返回所有方法<br />
	 * Returns a <code>Method</code> array of the methods to which instances of the specified
	 * respond except for those methods defined in the class specified by limit
	 * or any of its superclasses. Note that limit is usually used to eliminate
	 * them methods defined by <code>java.lang.Object</code>. If limit is <code>null</code> then all
	 * methods are returned.
	 * 
	 * @param clazz，当前类
	 * @param limit，限制到的超类
	 * @return 返回从当前类到限制类所有方法
	 */
	public static Method[] getSupportedMethods(Class<?> clazz, Class<?> limit) {
		ArrayList<Method> supportedMethods = new ArrayList<Method>();
		for (Class<?> c = clazz; c != limit; c = c.getSuperclass()) {
			Method[] methods = c.getDeclaredMethods();
			for (Method method : methods) {
				boolean found = false;
				for (Method supportedMethod : supportedMethods) {
					if (compareSignatures(method, supportedMethod)) {
						found = true;
						break;
					}
				}
				if (found == false) {
					supportedMethods.add(method);
				}
			}
		}
		return supportedMethods.toArray(new Method[supportedMethods.size()]);
	}

	/**
	 * 返回给定类的所有的属性，直到<code>Object.class</code>类
	 * 
	 * @param clazz，当前类
	 * @return 返回从clazz开始到Object中的所有的属性数组
	 */
	public static Field[] getSupportedFields(Class<?> clazz) {
		return getSupportedFields(clazz, Object.class);
	}
	/**
	 * 返回给定类的所有的属性，直到给定的超类limit
	 * 
	 * @param clazz，当前类
	 * @param limit，限制类
	 * @return 返回从clazz开始到limit中的所有的属性数组
	 */
	public static Field[] getSupportedFields(Class<?> clazz, Class<?> limit) {
		ArrayList<Field> supportedFields = new ArrayList<Field>();
		for (Class<?> c = clazz; c != limit; c = c.getSuperclass()) {
			Field[] fields = c.getDeclaredFields();
			for (Field field : fields) {
				boolean found = false;
				for (Field supportedField : supportedFields) {
					if (compareSignatures(field, supportedField)) {
						found = true;
						break;
					}
				}
				if (found == false) {
					supportedFields.add(field);
				}
			}
		}
		return supportedFields.toArray(new Field[supportedFields.size()]);
	}


	// ---------------------------------------------------------------- compare

	/**
	 * 判断两个方法是否完全一样，包括方法名字、参数列表和返回值类型
	 */
	public static boolean compareDeclarations(Method first, Method second) {
		if (first.getReturnType() != second.getReturnType()) {
			return false;
		}
		return compareSignatures(first, second);
	}

	/**
	 * 判断两个方法的签名是否相同，包括方法的名字和参数列表
	 */
	public static boolean compareSignatures(Method first, Method second) {
		if (first.getName().equals(second.getName()) == false) {
			return false;
		}
		return compareParameters(first.getParameterTypes(), second.getParameterTypes());
	}

	/**
	 * 判断两个构造函数是否相同，包括名字和参数列表
	 */
	public static boolean compareSignatures(Constructor<?> first, Constructor<?> second) {
		if (first.getName().equals(second.getName()) == false) {
			return false;
		}
		return compareParameters(first.getParameterTypes(), second.getParameterTypes());
	}
	/**
	 * 判断两个属性的名字是否相同
	 */
	public static boolean compareSignatures(Field first, Field second) {
		return first.getName().equals(second.getName());
	}



	/**
	 * 判断方法或者构造方法的参数类型数组是否相同
	 * 
	 * @param first，第一个参数列表
	 * @param second，第二个参数列表
	 * @return 判断第一个参数列表是否和第二个参数列表相同
	 */
	public static boolean compareParameters(Class<?>[] first, Class<?>[] second) {
		if (first.length != second.length) {
			return false;
		}
		for (int i = 0; i < first.length; i++) {
			if (first[i] != second[i]) {
				return false;
			}
		}
		return true;
	}



	// ---------------------------------------------------------------- force

	/**
	 * 检查对象是否可以访问，如果不能，设置为可以访问。SecurityException被忽略<br />
	 * Suppress access check against a reflection object. SecurityException is silently ignored.
	 * Checks first if the object is already accessible.
	 */
	public static void forceAccess(AccessibleObject accObject){
		if (accObject.isAccessible() == true) {
			return;
		}
		try {
			accObject.setAccessible(true);
		} catch (SecurityException sex) {
			// ignore
		}
	}

	// ---------------------------------------------------------------- is public

	/**
	 * 判断反射中用来描述成员变量、方法和构造方法访问特性的Member对象中描述的是否是共有。
	 */
	public static boolean isPublic(Member member) {
		return Modifier.isPublic(member.getModifiers());
	}

	/**
	 * 判断反射中用来描述成员变量、方法和构造方法访问特性的Member对象中描述的是否是共有。并且含有这个元素的类是否也同样是public
	 * 
	 * @param member，描述成员变量、方法和构造方法的访问相关属性的对象
	 * @return 如果Member描述为public并且类也是public，那么返回<code>true</code>
	 */
	public static boolean isPublicPublic(Member member) {
		if (Modifier.isPublic(member.getModifiers()) == true) {
			if (Modifier.isPublic(member.getDeclaringClass().getModifiers())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 如果当前Class是共有的，那么返回<code>true</code>
	 * 
	 */
	public static boolean isPublic(Class<?> c) {
		return Modifier.isPublic(c.getModifiers());
	}


	// ---------------------------------------------------------------- create


	/**
	 * 创建新的实例，包括创建没有默认构造函数的可变类。同时会检查是否是map，list，String，字符，Boolean或者数字。不可变的时候会被缓存，并且不会重新创建。
	 * 数组创建不会含有任何元素。使用一堆<code>if</code>方式实现，比使用<code>HashMap</code>要快
	 * 
	 */
	public static Object newInstance(Class<?> type) throws IllegalAccessException, InstantiationException {
		if (type.isPrimitive()) {
			if (type == int.class) {
				return Integer.valueOf(0);
			}
			if (type == long.class) {
				return Long.valueOf(0);
			}
			if (type == boolean.class) {
				return Boolean.FALSE;
			}
			if (type == float.class) {
				return Float.valueOf(0);
			}
			if (type == double.class) {
				return Double.valueOf(0);
			}
			if (type == byte.class) {
				return Byte.valueOf((byte) 0);
			}
			if (type == short.class) {
				return Short.valueOf((short) 0);
			}
			if (type == char.class) {
				return Character.valueOf((char) 0);
			}
			throw new IllegalArgumentException("Invalid primitive type: " + type);
		}
		if (type == Integer.class) {
			return Integer.valueOf(0);
		}
		if (type == String.class) {
			return "";
		}
		if (type == Long.class) {
			return Long.valueOf(0);
		}
		if (type == Boolean.class) {
			return Boolean.FALSE;
		}
		if (type == Float.class) {
			return 0.0f;
		}
		if (type == Double.class) {
			return 0.0;
		}

		if (type == Map.class) {
			return new HashMap<Object, Object>();
		}
		if (type == List.class) {
			return new ArrayList<Object>();
		}
		if (type == Set.class) {
			return new LinkedHashSet<Object>();
		}
		if (type == Collection.class) {
			return new ArrayList<Object>();
		}

		if (type == Byte.class) {
			return Byte.valueOf((byte) 0);
		}
		if (type == Short.class) {
			return Short.valueOf((short) 0);
		}
		if (type == Character.class) {
			return Character.valueOf((char) 0);
		}

		if (type.isEnum() == true) {
			return type.getEnumConstants()[0];
		}

		if (type.isArray() == true) {
			return Array.newInstance(type.getComponentType(), 0);
		}

		return type.newInstance();
	}


	// ---------------------------------------------------------------- misc

	/**
	 * 表示第一个Memeber是否从第二个Member中访问。通常是检测用来描述成员变量、方法和构造方法的Member所描述的这些元素是否在有继承关系的父子类或接口中定义。
	 * 如果是返回<code>true</code>
	 */
	public static boolean isAssignableFrom(Member member1, Member member2) {
		return member1.getDeclaringClass().isAssignableFrom(member2.getDeclaringClass());
	}

	/**
	 * 返回当前类的所有超类Class数组
	 */
	public static Class<?>[] getSuperclasses(Class<?> type) {
		int i = 0;
		for (Class<?> x = type.getSuperclass(); x != null; x = x.getSuperclass()) {
			i++;
		}
		Class<?>[] result = new Class[i];
		i = 0;
		for (Class<?> x = type.getSuperclass(); x != null; x = x.getSuperclass()) {
			result[i] = x;
			i++;
		}
		return result;
	}

	/**
	 * 判断方法是否是用户定义的方法，而不是在<code>Object</code>中定义的方法。
	 * 如果方法不是Object中的方法会返回true
	 */
	public static boolean isUserDefinedMethod(final Method method) {
		return method.getDeclaringClass() != Object.class;
	}

	/**
	 * 判断方法是否是用户定义的方法，而不是在<code>Object</code>中定义的方法。
	 * 如果方法是Object中的方法会返回true
	 */
	public static boolean isObjectMethod(final Method method) {
		return method.getDeclaringClass() == Object.class;
	}


	/**
	 * 判断方法是否是bean的属性访问器，即是否是getter和setter方法
	 * 如果是返回<code>true</code>
	 */
	public static boolean isBeanProperty(Method method) {
		if (isObjectMethod(method)) {
			return false;
		}
		String methodName = method.getName();
		Class<?> returnType = method.getReturnType();
		Class<?>[] paramTypes =  method.getParameterTypes();
		if (methodName.startsWith(METHOD_GET_PREFIX)) {		// getter method must starts with 'get' and it is not getClass()
			if ((returnType != null) && (paramTypes.length == 0)) {	// getter must have a return type and no arguments
				return true;
			}
		} else if (methodName.startsWith(METHOD_IS_PREFIX)) {		    // ister must starts with 'is'
			if ((returnType != null)  && (paramTypes.length == 0)) {	// ister must have return type and no arguments
				return true;
			}
		} else if (methodName.startsWith(METHOD_SET_PREFIX)) {	// setter must start with a 'set'
			if (paramTypes.length == 1) {				        // setter must have just one argument
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断方法是否是bean的getter方法
	 * 如果是bean的getter方法返回<code>true</code>
	 */
	public static boolean isBeanPropertyGetter(Method method) {
		return getBeanPropertyGetterPrefixLength(method) != 0;
	}

	private static int getBeanPropertyGetterPrefixLength(Method method) {
		if (isObjectMethod(method)) {
			return 0;
		}
		String methodName = method.getName();
		Class<?> returnType = method.getReturnType();
		Class<?>[] paramTypes =  method.getParameterTypes();
		if (methodName.startsWith(METHOD_GET_PREFIX)) {		        // getter method must starts with 'get' and it is not getClass()
			if ((returnType != null) && (paramTypes.length == 0)) {	// getter must have a return type and no arguments
				return 3;
			}
		} else if (methodName.startsWith(METHOD_IS_PREFIX)) {		    // ister must starts with 'is'
			if ((returnType != null)  && (paramTypes.length == 0)) {	// ister must have return type and no arguments
				return 2;
			}
		}
		return 0;
	}

	/**
	 * 返回bean中getter方法所对应的名字。<br />
	 * Returns beans property getter name or <code>null</code> if method is not a real getter.
	 */
	public static String getBeanPropertyGetterName(Method method) {
		int prefixLength = getBeanPropertyGetterPrefixLength(method);
		if (prefixLength == 0) {
			return null;
		}
		String methodName = method.getName().substring(prefixLength);
		return Introspector.decapitalize(methodName);
	}

	/**
	 * 判断给定的方法是否是bean的setter方法，如果是返回<code>true</code>
	 */
	public static boolean isBeanPropertySetter(Method method) {
		return getBeanPropertySetterPrefixLength(method) != 0;
	}

	private static int getBeanPropertySetterPrefixLength(Method method) {
		if (isObjectMethod(method)) {
			return 0;
		}
		String methodName = method.getName();
		Class<?>[] paramTypes =  method.getParameterTypes();
		if (methodName.startsWith(METHOD_SET_PREFIX)) {	        // setter must start with a 'set'
			if (paramTypes.length == 1) {				        // setter must have just one argument
				return 3;
			}
		}
		return 0;
	}

	/**
	 * 返回bean属性setter方法所对应的属性的名字。如果不是一个setter方法，返回<code>null</code>
	 * Returns beans property setter name or <code>null</code> if method is not a real setter.
	 */
	public static String getBeanPropertySetterName(Method method) {
		int prefixLength = getBeanPropertySetterPrefixLength(method);
		if (prefixLength == 0) {
			return null;
		}
		String methodName = method.getName().substring(prefixLength);
		return Introspector.decapitalize(methodName);
	}

	// ---------------------------------------------------------------- generics

	/**
	 * Returns component type of the given <code>type</code>.
	 * For <code>ParameterizedType</code> it returns the last type in array.
	 */
	public static Class<?> getComponentType(Type type) {
		return getComponentType(type, -1);
	}

	/**
	 * 
	 * Returns the component type of the given <code>type</code>.<br>
	 * For example the following types all have the component-type MyClass:
	 * <ul>
	 * <li>MyClass[]</li>
	 * <li>List&lt;MyClass&gt;</li>
	 * <li>Foo&lt;? extends MyClass&gt;</li>
	 * <li>Bar&lt;? super MyClass&gt;</li>
	 * <li>&lt;T extends MyClass&gt; T[]</li>
	 * </ul>
	 *
	 * @param type is the type where to get the component type from.
	 * @return the component type of the given <code>type</code> or
	 *         <code>null</code> if the given <code>type</code> does NOT have
	 *         a single (component) type.
	 */
	public static Class<?> getComponentType(Type type, int index) {
		if (type instanceof Class) {
			Class<?> clazz = (Class<?>) type;
			if (clazz.isArray()) {
				return clazz.getComponentType();
			}
		} else if (type instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) type;
			Type[] generics = pt.getActualTypeArguments();
			if (index < 0) {
				index = generics.length + index;
			}
			if (index < generics.length) {
				return toClass(generics[index]);
			}
		} else if (type instanceof GenericArrayType) {
			GenericArrayType gat = (GenericArrayType) type;
			return toClass(gat.getGenericComponentType());
		}
		return null;
	}

	public static Class<?> getGenericSupertype(Class<?> type, int index) {
		return getComponentType(type.getGenericSuperclass(), index);
	}

	public static Class<?> getGenericSupertype(Class<?> type) {
		return getComponentType(type.getGenericSuperclass());
	}


	/**
	 * Returns {@link Class} for the given <code>type</code>.<br>
	 * Examples: <br>
	 * <table border="1">
	 * <tr>
	 * <th><code>type</code></th>
	 * <th><code>getSimpleType(type)</code></th>
	 * </tr>
	 * <tr>
	 * <td><code>String</code></td>
	 * <td><code>String</code></td>
	 * </td>
	 * <tr>
	 * <td><code>List&lt;String&gt;</code></td>
	 * <td><code>List</code></td>
	 * </td>
	 * <tr>
	 * <td><code>&lt;T extends MyClass&gt; T[]</code></td>
	 * <td><code>MyClass[]</code></td>
	 * </td>
	 * </table>
	 *
	 * @param type is the type to convert.
	 * @return the closest class representing the given <code>type</code>.
	 */
	public static Class<?> toClass(Type type) {
		if (type instanceof Class) {
			return (Class<?>) type;
		}
		if (type instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) type;
			return toClass(pt.getRawType());
		}
		if (type instanceof WildcardType) {
			WildcardType wt = (WildcardType) type;
			Type[] lower = wt.getLowerBounds();
			if (lower.length == 1) {
				return toClass(lower[0]);
			}
			Type[] upper = wt.getUpperBounds();
			if (upper.length == 1) {
				return toClass(upper[0]);
			}
		} else if (type instanceof GenericArrayType) {
			GenericArrayType gat = (GenericArrayType) type;
			Class<?> componentType = toClass(gat.getGenericComponentType());
			// this is sort of stupid but there seems no other way...
			return Array.newInstance(componentType, 0).getClass();
		} else if (type instanceof TypeVariable) {
			TypeVariable<?> tv = (TypeVariable<?>) type;
			Type[] bounds = tv.getBounds();
			if (bounds.length == 1) {
				return toClass(bounds[0]);
			}
		}
		return null;
	}


	// ---------------------------------------------------------------- annotations

	/**
	 * Reads annotation value. Returns <code>null</code> on error.
	 */
	public static Object readAnnotationValue(Annotation annotation, String name) {
		try {
			Method method  = annotation.annotationType().getDeclaredMethod(name);
			return method.invoke(annotation);
		} catch (Exception ignore) {
			return null;
		}
	}

	/**
	 * 反射获取bean的key和value的map
	 *
	 * @param bean
	 * @return
	 */
	public static Map<String, Object> getBeanKVMap(Object bean) {
		//使用LinkedHashMap是为了保持为变量顺序
		Map<String, Object> kvMap = new LinkedHashMap<>();
		Field fields[] = bean.getClass().getDeclaredFields();
		for (Field f : fields) {
			if (!Modifier.isStatic(f.getModifiers())) {
				try {
					f.setAccessible(true); // 抑制Java对修饰符的检查
					kvMap.put(f.getName(), f.get(bean));
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		return kvMap;
	}
}