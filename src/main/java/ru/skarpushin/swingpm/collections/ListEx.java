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
package ru.skarpushin.swingpm.collections;

import java.util.List;

public interface ListEx<E> extends List<E>, HasListExEvents<E> {
	void fireItemChanged(E item);

	/**
	 * Returning read-only view of this list which will containing only those items
	 * which considered as suitable by filterPredicate
	 * 
	 * @param filterPredicate
	 *            predicate which decide if list item is suitable for new view or
	 *            not
	 * @return readonly view of underlying list
	 */
	ListEx<E> getView(FilterPredicate<E> filterPredicate);

}
