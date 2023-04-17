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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.summerb.validation.ValidationError;

import ru.skarpushin.swingpm.collections.ListEx;
import ru.skarpushin.swingpm.collections.ListExEventListener;
import ru.skarpushin.swingpm.valueadapters.ValueAdapter;

/**
 * Represents model for multiple selection in list of optionsAccessor.
 * 
 * This model is not complete solution because it's eavily depends on view how
 * it will bind this model to it' components and listers.
 * 
 * @author sergeyk
 * 
 * @param <E>
 */
public class ModelMultSelInListProperty<E> extends ModelListProperty<E> {
	// private static Logger log =
	// Logger.getLogger(ModelMultSelInListProperty.class);

	private final ModelListProperty<E> options;

	public ModelMultSelInListProperty(Object source, ValueAdapter<List<E>> valueAdapter, String propertyName,
			ModelListProperty<E> options) {
		this(source, valueAdapter, propertyName, options, null);
	}

	public ModelMultSelInListProperty(Object source, ValueAdapter<List<E>> valueAdapter, String propertyName,
			ModelListProperty<E> options, ListEx<ValidationError> veSource) {
		super(source, valueAdapter, propertyName, veSource);
		this.options = options;

		// Monitor changes in parent list
		options.getList().addListExEventListener(optionsListChangesHandler);
	}

	private ListExEventListener<E> optionsListChangesHandler = new ListExEventListener<E>() {
		@Override
		public void onItemAdded(E item, int atIndex) {
			// ignore //TODO: WHY?!!!!
		}

		@Override
		public void onItemChanged(E item, int atIndex) {
			// ignore //TODO: WHY?!!!!
		}

		@Override
		public void onItemRemoved(E item, int wasAtIndex) {
			getList().remove(item);
		}

		@Override
		public void onAllItemsRemoved(int sizeWas) {
			getList().clear();
		}
	};

	private ModelMultSelInListPropertyAccessor<E> modelMultSelInListPropertyAccessor = new ModelMultSelInListPropertyAccessor<E>() {
		@Override
		public ModelListPropertyAccessor<E> getOptions() {
			return options.getModelListPropertyAccessor();
		}

		@SuppressWarnings("unchecked")
		@Override
		public void setNewSelection(Object[] selectedObjects) {
			// log.debug("setNewSelection(): " +
			// Arrays.toString((selectedObjects)));

			if (selectedObjects == null || selectedObjects.length == 0) {
				// log.debug("setNewSelection(): clear()");
				getList().clear();
				return;
			}

			List<Object> newSelection = new ArrayList<Object>(Arrays.asList(selectedObjects));

			// modify our list according to selected indices
			for (int i = getList().size() - 1; i >= 0; i--) {
				E prevSelItem = getList().get(i);

				if (!newSelection.remove(prevSelItem)) {
					// if were unable to remove it from new selection, that mean
					// it's not there
					// log.debug("setNewSelection(): remove()");
					getList().remove(i);
					continue;
				} else {
					// the item in new sel and in old... no changes
				}
			}

			// if newSel is not empty - than we have new items
			for (Object item : newSelection) {
				if (!options.getList().contains(item)) {
					// Should we throw an error in that cae?...
					continue;
				}
				// log.debug("setNewSelection(): add()");
				getList().add((E) item);
			}
		}

		@Override
		public ModelListPropertyAccessor<E> getSelectionAccessor() {
			return ModelMultSelInListProperty.this.getModelListPropertyAccessor();
		}

		@Override
		public void addListExEventListener(ListExEventListener<E> l) {
			getList().addListExEventListener(l);
		}

		@Override
		public void removeListExEventListener(ListExEventListener<E> l) {
			getList().removeListExEventListener(l);
		}

		@Override
		public ListEx<ValidationError> getValidationErrors() {
			return getModelListPropertyAccessor().getValidationErrors();
		}
	};

	public ModelMultSelInListPropertyAccessor<E> getModelMultSelInListPropertyAccessor() {
		return modelMultSelInListPropertyAccessor;
	}
}
