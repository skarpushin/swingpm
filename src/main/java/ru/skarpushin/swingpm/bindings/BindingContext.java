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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JToggleButton;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import ru.skarpushin.swingpm.base.HasValidationErrorsListEx;
import ru.skarpushin.swingpm.modelprops.ModelPropertyAccessor;
import ru.skarpushin.swingpm.modelprops.lists.ModelMultSelInListPropertyAccessor;
import ru.skarpushin.swingpm.modelprops.lists.ModelSelInComboBoxPropertyAccessor;

public class BindingContext {
	private List<Binding> bindings = new ArrayList<Binding>();

	protected BindingContext() {

	}

	/**
	 * @deprecated avoid using this approach cause it uses hard coded literal of
	 *             target method name. Use
	 *             {@link #registerOnChangeHandler(ModelPropertyAccessor, TypedPropertyChangeListener)}
	 *             instead
	 */
	@Deprecated
	public Binding registerOnChangeHandler(ModelPropertyAccessor<?> property, Object targetObject, String methodName) {
		Binding ret = StaticBinding.registerOnChangeHandler(property, targetObject, methodName);
		bindings.add(ret);
		return ret;
	}

	public <T> Binding registerOnChangeHandler(ModelPropertyAccessor<T> property,
			TypedPropertyChangeListener<T> listener) {
		Binding ret = StaticBinding.registerOnChangeHandler(property, listener);
		bindings.add(ret);
		return ret;
	}

	public Binding registerPropertyValuePropagation(ModelPropertyAccessor<?> property, Object targetObject,
			String targetProperty) {
		Binding ret = StaticBinding.registerPropertyValuePropagation(property, targetObject, targetProperty);
		bindings.add(ret);
		return ret;
	}

	public void unbindAll() {
		for (Iterator<Binding> iter = bindings.iterator(); iter.hasNext();) {
			Binding b = iter.next();
			if (b.isBound()) {
				b.unbind();
			}

			iter.remove();
		}
	}

	public Binding registerTextPropertyBinding(ModelPropertyAccessor<String> property, Document targetObject) {
		Binding ret = StaticBinding.registerTextPropertyBinding(property, targetObject);
		bindings.add(ret);
		return ret;
	}

	public void add(Binding ret) {
		bindings.add(ret);
	}

	public void setupBinding(ModelPropertyAccessor<String> textProperty, JTextComponent textComponent) {
		registerTextPropertyBinding(textProperty, textComponent.getDocument());
		createValidationErrorsViewIfAny(textProperty, textComponent);
	}

	public void createValidationErrorsViewIfAny(HasValidationErrorsListEx validationErrorsSource,
			JComponent component) {
		if (validationErrorsSource.getValidationErrors() == null) {
			return;
		}

		constructValidationErrorsBinding(validationErrorsSource, component);
	}

	protected void constructValidationErrorsBinding(HasValidationErrorsListEx validationErrorsSource,
			JComponent component) {
		// ValidationErrorsBalloonView veview = new
		// ValidationErrorsBalloonView();
		// veview.renderTo(null, component);
		// veview.setPm(validationErrorsSource.getValidationErrors());
		// add(veview);
	}

	public void setupBinding(ModelSelInComboBoxPropertyAccessor<?> comboBoxModel, JComboBox comboBox) {
		add(new ModelSelInComboBoxBinding(this, comboBoxModel, comboBox));
	}

	public void setupBinding(ModelSelInComboBoxPropertyAccessor<?> comboBoxModel, JList singleSelList) {
		add(new ModelSelInListBinding(this, comboBoxModel, singleSelList));
	}

	public <E> void setupBinding(ModelMultSelInListPropertyAccessor<E> modelProperty, JList list) {
		add(new ModelMultSelInListBinding<E>(this, modelProperty, list));
	}

	public void setupBinding(ModelPropertyAccessor<String> stringProperty, JLabel label) {
		add(new JLabelBinding(this, stringProperty, label));
	}

	public void setupBinding(Action action, AbstractButton trigger) {
		add(new ActionBinding(action, trigger));
	}

	public void setupBinding(ModelPropertyAccessor<Boolean> booleanProperty, JToggleButton toggleButton) {
		add(new ToggleButtonBinding(booleanProperty, toggleButton));
	}
}
