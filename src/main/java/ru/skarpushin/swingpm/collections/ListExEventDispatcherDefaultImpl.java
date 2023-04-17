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

import com.google.common.base.Preconditions;

/**
 * Default impl which will just notify all listeners
 * 
 * @author sergey.karpushin
 * 
 * @param <E>
 */
public class ListExEventDispatcherDefaultImpl<E> implements ListExEventListener<E> {
	private final ListExBase<E> list;

	public ListExEventDispatcherDefaultImpl(ListExBase<E> list) {
		Preconditions.checkArgument(list != null);
		this.list = list;
	}

	@Override
	public void onItemAdded(E item, int atIndex) {
		for (ListExEventListener<E> listener : list.listeners) {
			listener.onItemAdded(item, atIndex);
		}
	}

	@Override
	public void onItemChanged(E item, int atIndex) {
		for (ListExEventListener<E> listener : list.listeners) {
			listener.onItemChanged(item, atIndex);
		}
	}

	@Override
	public void onItemRemoved(E item, int wasAtIndex) {
		for (ListExEventListener<E> listener : list.listeners) {
			listener.onItemRemoved(item, wasAtIndex);
		}
	}

	@Override
	public void onAllItemsRemoved(int sizeWas) {
		for (ListExEventListener<E> listener : list.listeners) {
			listener.onAllItemsRemoved(sizeWas);
		}
	}

	protected boolean isListenerCompliant(Object[] listeners, int i) {
		return listeners[i] == ListExEventListener.class;
	}

}
