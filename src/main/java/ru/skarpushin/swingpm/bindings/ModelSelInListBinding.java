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

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.google.common.base.Preconditions;

import ru.skarpushin.swingpm.modelprops.lists.ModelSelInComboBoxPropertyAccessor;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ModelSelInListBinding implements Binding, ListDataListener, ListSelectionListener {
	private final ModelSelInComboBoxPropertyAccessor<?> model;
	private JList list;

	public ModelSelInListBinding(BindingContext bindingContext, ModelSelInComboBoxPropertyAccessor<?> nodel,
			JList list) {
		this.model = nodel;
		this.list = list;
		Preconditions.checkArgument(bindingContext != null);
		Preconditions.checkArgument(nodel != null);
		Preconditions.checkArgument(list != null);
		Preconditions.checkArgument(list.getSelectionMode() == ListSelectionModel.SINGLE_SELECTION);

		list.setModel(nodel);
		list.setSelectedValue(nodel.getSelectedItem(), true);
		list.addListSelectionListener(this);
		nodel.addListDataListener(this);

		bindingContext.createValidationErrorsViewIfAny(model, list);
	}

	@Override
	public boolean isBound() {
		return list != null;
	}

	@Override
	public void unbind() {
		model.removeListDataListener(this);
		list.removeListSelectionListener(this);
		list.setModel(new DefaultListModel());
		list = null;
	}

	@Override
	public void intervalAdded(ListDataEvent e) {
		list.setSelectedValue(model.getSelectedItem(), true);
	}

	@Override
	public void intervalRemoved(ListDataEvent e) {
		list.setSelectedValue(model.getSelectedItem(), true);
	}

	@Override
	public void contentsChanged(ListDataEvent e) {
		list.setSelectedValue(model.getSelectedItem(), true);
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting() || !isBound()) {
			return;
		}

		model.setSelectedItem(list.getSelectedValue());
	}

}
