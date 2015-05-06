package animeRecommendationEnginer.server.reflectionManager.contracts;

import javax.ejb.Local;

/**
 * This interface defines the contract for reflection services.
 * 
 * @author tejasvamsingh
 *
 */
@Local
public interface IReflectionManager {

	public Object getMyBeanFromClassName(String className);

	public Object invokeMethod(Object classObject, String methodName,
			Object parameterClass, String parameterType);
}
