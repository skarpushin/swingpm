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

import org.summerb.validation.ValidationError;

import com.google.common.base.Preconditions;

import ru.skarpushin.swingpm.collections.ListEx;
import ru.skarpushin.swingpm.modelprops.ModelProperty;
import ru.skarpushin.swingpm.valueadapters.ValueAdapter;

/**
 * Model which represents selection in combo property
 * 
 * @author sergeyk
 * 
 */
public class ModelSelInComboBoxProperty<E> extends ModelProperty<E> {
	protected final ModelListPropertyAccessor<E> optionsAccessor;
	protected final ModelListProperty<E> optionsProperty;
	private ModelSelInComboBoxPropertyAccessor<E> modelSelInComboBoxPropertyAccessor;

	public ModelSelInComboBoxProperty(Object source, ValueAdapter<E> valueAdapter, String propertyName,
			ModelListProperty<E> options) {
		this(source, valueAdapter, propertyName, options, null);
	}

	public ModelSelInComboBoxProperty(Object source, ValueAdapter<E> valueAdapter, String propertyName,
			ModelListProperty<E> options, ListEx<ValidationError> veSource) {
		super(source, valueAdapter, propertyName, veSource);
		Preconditions.checkArgument(options != null);

		this.optionsProperty = options;
		this.optionsAccessor = options.getModelListPropertyAccessor();

		// TODO: P3: Should we track if oprions list changed and currently
		// selected
		// item no more an option?
	}

	@Override
	public boolean setValueByOwner(E value) {
		if (!super.setValueByOwner(value)) {
			return false;
		}

		getModelSelInComboBoxPropertyAccessor().setSelectedItem(value);

		return true;
	};

	public ModelSelInComboBoxPropertyAccessor<E> getModelSelInComboBoxPropertyAccessor() {
		if (modelSelInComboBoxPropertyAccessor == null) {
			modelSelInComboBoxPropertyAccessor = new ComboBoxDefaultAccessorStrategy<E>(this);
		}
		return modelSelInComboBoxPropertyAccessor;
	}

	public void setModelSelInComboBoxPropertyAccessor(ModelSelInComboBoxPropertyAccessor<E> value) {
		modelSelInComboBoxPropertyAccessor = value;
	}

}
