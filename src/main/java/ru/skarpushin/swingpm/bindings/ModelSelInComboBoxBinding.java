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

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import com.google.common.base.Preconditions;

import ru.skarpushin.swingpm.modelprops.lists.ModelSelInComboBoxPropertyAccessor;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ModelSelInComboBoxBinding implements Binding {
	private JComboBox comboBox;

	public ModelSelInComboBoxBinding(BindingContext ctx, ModelSelInComboBoxPropertyAccessor<?> comboBoxModel,
			JComboBox comboBox) {
		this.comboBox = comboBox;

		comboBox.setModel(comboBoxModel);
		ctx.createValidationErrorsViewIfAny(comboBoxModel, comboBox);
	}

	@Override
	public boolean isBound() {
		return comboBox != null;
	}

	@Override
	public void unbind() {
		Preconditions.checkState(comboBox != null);

		comboBox.setModel(new DefaultComboBoxModel());
		comboBox = null;
	}
}
