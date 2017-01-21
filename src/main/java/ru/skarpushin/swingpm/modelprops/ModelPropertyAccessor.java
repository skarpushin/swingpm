package ru.skarpushin.swingpm.modelprops;

import java.beans.PropertyChangeListener;

import ru.skarpushin.swingpm.base.HasPropertyName;
import ru.skarpushin.swingpm.base.HasValidationErrorsListEx;
import ru.skarpushin.swingpm.valueadapters.ValueAdapter;

public interface ModelPropertyAccessor<E> extends ValueAdapter<E>, HasPropertyName, HasValidationErrorsListEx {

	void addPropertyChangeListener(PropertyChangeListener propertyChangeListener);

	void removePropertyChangeListener(PropertyChangeListener propertyChangeBoundHandler);
}
