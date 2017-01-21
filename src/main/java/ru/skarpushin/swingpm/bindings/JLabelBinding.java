package ru.skarpushin.swingpm.bindings;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JLabel;

import ru.skarpushin.swingpm.modelprops.ModelPropertyAccessor;

public class JLabelBinding implements Binding, PropertyChangeListener {
	// private static Logger log = Logger.getLogger(JLabelBinding.class);

	private final ModelPropertyAccessor<String> stringProperty;
	private JLabel label;

	public JLabelBinding(BindingContext bindingContext, ModelPropertyAccessor<String> stringProperty, JLabel label) {
		this.stringProperty = stringProperty;
		this.label = label;

		stringProperty.addPropertyChangeListener(this);
		label.setText(stringProperty.getValue() == null ? "" : stringProperty.getValue());
	}

	@Override
	public boolean isBound() {
		return label != null;
	}

	@Override
	public void unbind() {
		stringProperty.removePropertyChangeListener(this);
		label = null;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (label == null) {
			return;
		}

		label.setText(stringProperty.getValue() == null ? "" : stringProperty.getValue());
	}
}
