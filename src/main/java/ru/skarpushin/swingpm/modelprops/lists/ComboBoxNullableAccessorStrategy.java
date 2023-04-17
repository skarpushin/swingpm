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
package ru.skarpushin.swingpm.modelprops.lists;

import javax.swing.event.ListDataListener;

import org.summerb.validation.ValidationError;

import ru.skarpushin.swingpm.collections.ListEx;

public class ComboBoxNullableAccessorStrategy<E> implements ModelSelInComboBoxPropertyAccessor<E> {
	// TODO: Should we i18n it ?
	private Object nullItem = "   ";

	private final ModelSelInComboBoxProperty<E> property;

	public ComboBoxNullableAccessorStrategy(ModelSelInComboBoxProperty<E> property) {
		this.property = property;
	}

	@Override
	public E get(int idx) {
		if (idx == property.optionsAccessor.getSize()) {
			return null;
		}

		return property.optionsAccessor.get(idx);
	}

	@Override
	public int indexOf(E item) {
		if (item == null) {
			return property.optionsAccessor.getSize();
		}
		return property.optionsAccessor.indexOf(item);
	}

	@Override
	public int getSize() {
		return property.optionsAccessor.getSize() + 1;
	}

	@Override
	public Object getElementAt(int index) {
		if (index == property.optionsAccessor.getSize()) {
			return nullItem;
		}

		return property.optionsAccessor.getElementAt(index);
	}

	@Override
	public void addListDataListener(ListDataListener l) {
		property.optionsAccessor.addListDataListener(l);
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		property.optionsAccessor.removeListDataListener(l);
	}

	@Override
	public String getPropertyName() {
		return property.getPropertyName();
	}

	@Override
	public ListEx<ValidationError> getValidationErrors() {
		return property.getModelPropertyAccessor().getValidationErrors();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setSelectedItem(Object anItem) {
		Object valueToSet = anItem == nullItem ? null : anItem;

		if (!property.setValueByConsumer((E) valueToSet)) {
			return;
		}

		property.optionsProperty.getList().fireItemChanged(null);
	}

	@Override
	public Object getSelectedItem() {
		if (property.hasValue()) {
			return property.getValue();
		}

		return nullItem;
	}
}
