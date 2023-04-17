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

import ru.skarpushin.swingpm.base.HasValidationErrorsListEx;
import ru.skarpushin.swingpm.collections.HasListExEvents;

public interface ModelMultSelInListPropertyAccessor<E> extends HasListExEvents<E>, HasValidationErrorsListEx {
	/**
	 * Return all optionsAccessor we can choose from. Normally used to populate list
	 */
	ModelListPropertyAccessor<E> getOptions();

	/**
	 * Return managed list accessor which holds selection stateId
	 */
	ModelListPropertyAccessor<E> getSelectionAccessor();

	void setNewSelection(Object[] selectedObjects);
}
