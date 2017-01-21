package ru.skarpushin.swingpm.bindings;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.JToggleButton;

import org.apache.log4j.Logger;

import ru.skarpushin.swingpm.modelprops.ModelPropertyAccessor;
import ru.skarpushin.swingpm.tools.SwingPmSettings;
import ru.skarpushin.swingpm.tools.actions.LocalizedAction;

public class ToggleButtonBinding implements Binding {
	private static Logger log = Logger.getLogger(ToggleButtonBinding.class);

	private JToggleButton toggleButton;
	private ModelPropertyAccessor<Boolean> booleanProperty;
	private Action action;

	public ToggleButtonBinding(ModelPropertyAccessor<Boolean> booleanProperty, JToggleButton toggleButton) {
		this.booleanProperty = booleanProperty;
		this.toggleButton = toggleButton;

		toggleButton.setSelected(booleanProperty.getValue());

		toggleButton.setAction(getAction());
		booleanProperty.addPropertyChangeListener(propertyChangeListener);
	}

	private final PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			// log.trace(booleanProperty.getPropertyName() +
			// ": onPropertyChange = " + evt.getNewValue());
			toggleButton.getModel().setSelected((Boolean) evt.getNewValue());
		}
	};

	@SuppressWarnings("serial")
	private Action getAction() {
		if (action == null) {
			action = new LocalizedAction(getActionMessageCode()) {
				@Override
				public void actionPerformed(ActionEvent e) {
					// log.trace(booleanProperty.getPropertyName() +
					// ": onAction, stateId = " +
					// toggleButton.getModel().isSelected());
					booleanProperty.setValue(toggleButton.getModel().isSelected());
				}
			};
		}
		return action;
	}

	private String getActionMessageCode() {
		// NOTE: Not sure that this is clean solution. Probably we should
		// have separate ModelProperty which will explicitly provide check
		// box title
		String propName = booleanProperty.getPropertyName();

		String alternatePropName = "term." + propName;
		String actionTitle = SwingPmSettings.getMessages().get(alternatePropName);
		if (!actionTitle.equals(alternatePropName)) {
			return alternatePropName;
		}

		actionTitle = SwingPmSettings.getMessages().get(propName);
		if (!actionTitle.equals(propName)) {
			return propName;
		}

		log.error("No message code defined for property name: " + propName);
		return propName;
	}

	@Override
	public boolean isBound() {
		return toggleButton != null;
	}

	@Override
	public void unbind() {
		toggleButton.setAction(null);
		booleanProperty.removePropertyChangeListener(propertyChangeListener);
		toggleButton = null;
	}

}
