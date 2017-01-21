package ru.skarpushin.swingpm.bindings;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import ru.skarpushin.swingpm.modelprops.ModelPropertyAccessor;

public class PropertyChangeHandlerBinding<T> implements Binding, PropertyChangeListener {
	private final ModelPropertyAccessor<T> property;
	private TypedPropertyChangeListener<T> listener;

	public PropertyChangeHandlerBinding(ModelPropertyAccessor<T> property, TypedPropertyChangeListener<T> listener) {
		this.property = property;
		this.listener = listener;

		property.addPropertyChangeListener(this);
	}

	@Override
	public boolean isBound() {
		return listener != null;
	}

	@Override
	public void unbind() {
		listener = null;
		property.removePropertyChangeListener(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (listener == null) {
			return;
		}

		listener.handlePropertyChanged(evt.getSource(), property.getPropertyName(), (T) evt.getOldValue(),
				(T) evt.getNewValue());
	}
}
