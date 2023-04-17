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

import com.google.common.base.Preconditions;

import ru.skarpushin.swingpm.modelprops.ModelPropertyAccessor;
import ru.skarpushin.swingpm.valueadapters.ValueAdapter;

@SuppressWarnings("rawtypes")
public class PropertyValuePropagationBinding implements Binding, PropertyChangeListener {
	private final ModelPropertyAccessor<?> source;
	private ValueAdapter destination;

	@SuppressWarnings("unchecked")
	public PropertyValuePropagationBinding(ModelPropertyAccessor source, ValueAdapter destination) {
		Preconditions.checkArgument(source != null);
		Preconditions.checkArgument(destination != null);

		this.source = source;
		this.destination = destination;

		destination.setValue(source.getValue());

		source.addPropertyChangeListener(this);
	}

	@Override
	public boolean isBound() {
		return destination != null;
	}

	@Override
	public void unbind() {
		destination = null;
		source.removePropertyChangeListener(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (destination == null || !source.getPropertyName().equals(evt.getPropertyName())) {
			return;
		}

		try {
			// NOTE: Do not check if destination already has this value. If it
			// cares - it will do this check, we shouldn't care about it
			destination.setValue(evt.getNewValue());
		} catch (Throwable e) {
			throw new RuntimeException(
					"Failed to propagate " + source.getPropertyName() + " property value " + evt.getNewValue(), e);
		}
	}

}
