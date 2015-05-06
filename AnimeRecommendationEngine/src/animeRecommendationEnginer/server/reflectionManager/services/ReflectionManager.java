package animeRecommendationEnginer.server.reflectionManager.services;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import animeRecommendationEnginer.server.reflectionManager.contracts.IReflectionManager;

@Stateless
public class ReflectionManager implements IReflectionManager {

	@Inject
	@Any
	Instance<Object> myBeans;

	public Object getMyBeanFromClassName(String className) {
		Class<?> clazz = null;
		try {
			clazz = Class.forName(className);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return myBeans.select(clazz).get();
	}

	@Override
	public Object invokeMethod(Object classObject, String methodName,
			Object parameters, String ParameterType) {

		try {
			Method method = null;
			if (ParameterType.equals("Map"))
				method = classObject.getClass().getDeclaredMethod(methodName,
						Map.class);
			else if (ParameterType.equals("List"))
				method = classObject.getClass().getDeclaredMethod(methodName,
						List.class);

			if (method != null)
				return method.invoke(classObject, parameters);

		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
