/*******************************************************************************
 * Copyright 2015-2023 Sergey Karpushin
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package ru.skarpushin.swingpm.valueadapters;

import java.lang.reflect.Method;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

/**
 * Adapter which redirects getter and setter to target object
 * 
 * @author sergeyk
 * 
 * @param <E>
 */
public class ValueAdapterReflectionImpl<E> implements ValueAdapter<E> {
	private static Logger log = LogManager.getLogger(ValueAdapterReflectionImpl.class);

	private final Object target;
	private Method readMethod;
	private Method writeMethod;
	private final String propertyName;

	public ValueAdapterReflectionImpl(Object target, String propertyName) {
		Preconditions.checkArgument(target != null);
		Preconditions.checkArgument(!Strings.isNullOrEmpty(propertyName));

		this.target = target;
		this.propertyName = propertyName;

		try {
			Class c = target.getClass();
			String capitalizedName = propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
			readMethod = findMethod(c, "get" + capitalizedName);
			if (readMethod == null) {
				readMethod = findMethod(c, "is" + capitalizedName);
			}
			Preconditions.checkArgument(readMethod != null,
					"Didn't find getter for '" + capitalizedName + "' property on class" + c);

			writeMethod = findMethod(c, "set" + capitalizedName, readMethod.getReturnType());
			Preconditions.checkArgument(writeMethod != null,
					"Didn't find setter for '" + capitalizedName + "' property on class" + c);
		} catch (Throwable t) {
			throw new RuntimeException("Failed to init ValueAdapterReflectionImpl for " + target + "::" + propertyName,
					t);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Method findMethod(Class c, String methodName, Class... parameters) {
		try {
			return c.getMethod(methodName, parameters);
		} catch (Throwable t) {
			log.trace("Method " + methodName + " wasn't found on class " + c);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public E getValue() {
		try {
			return (E) readMethod.invoke(target);
		} catch (Throwable e) {
			throw new RuntimeException("Failed to get value for property: " + propertyName, e);
		}
	}

	@Override
	public void setValue(E value) {
		try {
			writeMethod.invoke(target, value);
		} catch (Throwable e) {
			throw new RuntimeException("Failed to set value for property: " + propertyName, e);
		}
	}

}
