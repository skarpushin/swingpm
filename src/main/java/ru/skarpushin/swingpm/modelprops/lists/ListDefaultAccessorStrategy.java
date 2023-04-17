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
package ru.skarpushin.swingpm.modelprops.lists;

import javax.swing.event.ListDataListener;

import org.summerb.validation.ValidationError;

import ru.skarpushin.swingpm.collections.ListEx;

public class ListDefaultAccessorStrategy<E> implements ModelListPropertyAccessor<E> {
	private final ModelListProperty<E> property;

	public ListDefaultAccessorStrategy(ModelListProperty<E> property) {
		this.property = property;
	}

	@Override
	public int getSize() {
		return property.list.size();
	}

	@Override
	public Object getElementAt(int index) {
		return property.list.get(index);
	}

	@Override
	public E get(int index) {
		return property.list.get(index);
	}

	@Override
	public int indexOf(E item) {
		return property.list.indexOf(item);
	}

	@Override
	public void addListDataListener(ListDataListener l) {
		property.listenerList.add(ListDataListener.class, l);
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		property.listenerList.remove(ListDataListener.class, l);
	}

	@Override
	public String getPropertyName() {
		return property.getPropertyName();
	}

	@Override
	public ListEx<ValidationError> getValidationErrors() {
		return property.getValidationErrors();
	}

}
