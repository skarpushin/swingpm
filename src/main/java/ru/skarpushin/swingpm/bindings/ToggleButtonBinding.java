package ru.skarpushin.swingpm.bindings;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JToggleButton;

import org.apache.log4j.Logger;

import ru.skarpushin.swingpm.modelprops.ModelPropertyAccessor;
import ru.skarpushin.swingpm.tools.SwingPmSettings;

public class ToggleButtonBinding implements Binding {
	private static Logger log = Logger.getLogger(ToggleButtonBinding.class);

	private JToggleButton toggleButton;
	private ModelPropertyAccessor<Boolean> booleanProperty;

	public ToggleButtonBinding(ModelPropertyAccessor<Boolean> booleanProperty, JToggleButton toggleButton) {
		this.booleanProperty = booleanProperty;
		this.toggleButton = toggleButton;

		toggleButton.setSelected(booleanProperty.getValue());

		toggleButton.setText(SwingPmSettings.getMessages().get(getActionMessageCode()));
		toggleButton.getModel().addItemListener(toggleButtonChangeListener);

		booleanProperty.addPropertyChangeListener(propertyChangeListener);
	}

	private final PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			log.debug(booleanProperty.getPropertyName() + ": onChange from PM = " + evt.getNewValue());
			toggleButton.getModel().setSelected((Boolean) evt.getNewValue());
		}
	};

	private ItemListener toggleButtonChangeListener = new ItemListener() {
		@Override
		public void itemStateChanged(ItemEvent e) {
			boolean isSelected = toggleButton.getModel().isSelected();
			log.debug(booleanProperty.getPropertyName() + ": onChanged from UI, stateId = " + isSelected);
			booleanProperty.setValue(isSelected);
		}
	};

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
		toggleButton.setText("");
		toggleButton.getModel().removeItemListener(toggleButtonChangeListener);
		booleanProperty.removePropertyChangeListener(propertyChangeListener);
		toggleButton = null;
	}

}
