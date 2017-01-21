package ru.skarpushin.swingpm.bindings;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;

import com.google.common.base.Preconditions;

import ru.skarpushin.swingpm.modelprops.ModelPropertyAccessor;

/**
 * @deprecated avoid using this approach cause it uses hard coded literal of
 *             target method name. Use {@link PropertyChangeHandlerBinding}
 *             instead
 * @author sergeyk
 * 
 */
@Deprecated
public class PropertyChangeHandlerReflectionBinding implements Binding, PropertyChangeListener {
	private static Logger log = Logger.getLogger(PropertyChangeHandlerReflectionBinding.class);

	private Method handlerMethod;
	private final ModelPropertyAccessor<?> property;
	private final Object targetObject;

	public PropertyChangeHandlerReflectionBinding(ModelPropertyAccessor<?> property, Object targetObject,
			Method handlerMethod) {
		Preconditions.checkArgument(property != null);
		Preconditions.checkArgument(targetObject != null);
		Preconditions.checkArgument(handlerMethod != null);

		this.property = property;
		this.targetObject = targetObject;
		this.handlerMethod = handlerMethod;

		property.addPropertyChangeListener(this);
	}

	@Override
	public boolean isBound() {
		return handlerMethod != null;
	}

	@Override
	public void unbind() {
		handlerMethod = null;
		property.removePropertyChangeListener(this);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (handlerMethod == null || !property.getPropertyName().equals(evt.getPropertyName())) {
			return;
		}

		try {
			handlerMethod.invoke(targetObject, evt.getSource(), evt.getOldValue(), evt.getNewValue());
		} catch (Throwable e) {
			log.error("Failed to execute property change handler method", e);
			throw new RuntimeException("Failed to execute property change handler method", e);
		}
	}

}
