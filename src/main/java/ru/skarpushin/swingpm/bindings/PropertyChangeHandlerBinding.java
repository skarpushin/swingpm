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
