/*******************************************************************************
 * Copyright 2015-2021 Sergey Karpushin
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
package ru.skarpushin.swingpm.bindings;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JLabel;

import ru.skarpushin.swingpm.modelprops.ModelPropertyAccessor;

public class JLabelBinding implements Binding, PropertyChangeListener {
	// private static Logger log = LogManager.getLogger(JLabelBinding.class);

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
