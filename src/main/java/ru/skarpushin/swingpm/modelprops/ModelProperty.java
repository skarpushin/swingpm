package ru.skarpushin.swingpm.modelprops;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.SwingUtilities;

import org.summerb.approaches.validation.ValidationError;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import ru.skarpushin.swingpm.collections.FilterPredicate;
import ru.skarpushin.swingpm.collections.ListEx;
import ru.skarpushin.swingpm.tools.edt.Edt;
import ru.skarpushin.swingpm.valueadapters.ValueAdapter;

/**
 * Convenient class to hold property by property owner
 * 
 * @author sergeyk
 * 
 */
public class ModelProperty<E> implements FilterPredicate<ValidationError> {
	// private static Logger log = Logger.getLogger(ModelProperty.class);

	protected final Object source;
	protected final String propertyName;
	protected final ValueAdapter<E> valueAdapter;
	private PropertyChangeSupport propertyChangeSupport;
	private boolean fireEventsInEventDispatchingThread = true;
	protected ListEx<ValidationError> validationErrors;

	public ModelProperty(Object source, ValueAdapter<E> valueAdapter, String propertyName) {
		this(source, valueAdapter, propertyName, null);
	}

	public ModelProperty(Object source, ValueAdapter<E> valueAdapter, String propertyName,
			ListEx<ValidationError> veSource) {
		Preconditions.checkArgument(valueAdapter != null);
		Preconditions.checkArgument(Strings.isNullOrEmpty(propertyName));

		this.source = source;
		this.valueAdapter = valueAdapter;
		this.propertyName = propertyName;

		if (veSource != null) {
			validationErrors = veSource.getView(this);
		}

		this.propertyChangeSupport = new PropertyChangeSupport(source);
	}

	@Override
	public boolean isSuitable(ValidationError subject) {
		return propertyName.equals(subject.getFieldToken());
	}

	/**
	 * Intended to be used by property owner to set property value
	 * 
	 * @param value
	 * @return true if value was updated, false if value left unchanged (no
	 *         events fired)
	 */
	public boolean setValueByOwner(E value) {
		E oldValue = valueAdapter.getValue();
		if (isSameValue(oldValue, value)) {
			return false;
		}

		valueAdapter.setValue(value);
		firePropertyChanged(oldValue, value);
		return true;
	}

	/**
	 * Intended to be overridden to handle changes came from consumer (normally
	 * - UI layer)
	 * 
	 * @param value
	 * @return true if value was updated, false if value left unchanged (no
	 *         events fired)
	 * @deprecated USE IT ONLY FROM CLASS WHICH OWNS THIS PROPERTY!!! Not
	 *             actually deprecated, want to avoid misuse.
	 */
	@Deprecated
	public boolean setValueByConsumer(E value) {
		return setValueByOwner(value);
	}

	private boolean isSameValue(E a, E b) {
		if (a == b) {
			return true;
		}
		if (a == null || b == null) {
			return false;
		}
		return a.equals(b);
	}

	public void firePropertyChanged(E oldValue, E value) {
		final PropertyChangeEvent evt = new PropertyChangeEvent(source, propertyName, oldValue, value);

		if (isShouldDefferEventDispatchToEdtThread()) {
			try {
				Edt.invokeOnEdtAndWait(new Runnable() {
					@Override
					public void run() {
						propertyChangeSupport.firePropertyChange(evt);
					}
				});
			} catch (Throwable e) {
				throw new RuntimeException("Failed to fire proeprty " + propertyName + " change", e);
			}
		} else {
			propertyChangeSupport.firePropertyChange(evt);
		}
	}

	public void firePropertyChanged() {
		firePropertyChanged(null, valueAdapter.getValue());
	}

	private boolean isShouldDefferEventDispatchToEdtThread() {
		return fireEventsInEventDispatchingThread && !SwingUtilities.isEventDispatchThread();
	}

	public E getValue() {
		return valueAdapter.getValue();
	}

	private final ModelPropertyAccessor<E> modelPropertyAccessor = new ModelPropertyAccessor<E>() {
		@Override
		public E getValue() {
			return ModelProperty.this.getValue();
		}

		@Override
		public void setValue(E value) {
			setValueByConsumer(value);
		}

		@Override
		public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
			propertyChangeSupport.addPropertyChangeListener(propertyName, propertyChangeListener);
		}

		@Override
		public void removePropertyChangeListener(PropertyChangeListener propertyChangeBoundHandler) {
			propertyChangeSupport.removePropertyChangeListener(propertyChangeBoundHandler);
		}

		@Override
		public String getPropertyName() {
			return ModelProperty.this.getPropertyName();
		}

		@Override
		public ListEx<ValidationError> getValidationErrors() {
			return validationErrors;
		}
	};

	public String getPropertyName() {
		return propertyName;
	}

	public ModelPropertyAccessor<E> getModelPropertyAccessor() {
		return modelPropertyAccessor;
	}

	public boolean isFireEventsInEventDispatchingThread() {
		return fireEventsInEventDispatchingThread;
	}

	public void setFireEventsInEventDispatchingThread(boolean fireEventsInEventDispatchingThread) {
		this.fireEventsInEventDispatchingThread = fireEventsInEventDispatchingThread;
	}

	public boolean hasValue() {
		return getValue() != null;
	}

	public ListEx<ValidationError> getValidationErrors() {
		return validationErrors;
	}

}
