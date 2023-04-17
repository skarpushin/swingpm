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
package ru.skarpushin.swingpm.collections;

import javax.swing.SwingUtilities;

import ru.skarpushin.swingpm.tools.edt.Edt;

public class ListExEventDispatcherEdtWrapperImpl<E> implements ListExEventListener<E> {
	private final ListExEventListener<E> underlyningDispatcher;

	public ListExEventDispatcherEdtWrapperImpl(ListExEventListener<E> underlyningDispatcher) {
		this.underlyningDispatcher = underlyningDispatcher;
	}

	@Override
	public void onItemAdded(final E item, final int atIndex) {
		if (SwingUtilities.isEventDispatchThread()) {
			underlyningDispatcher.onItemAdded(item, atIndex);
		} else {
			try {
				Edt.invokeOnEdtAndWait(new Runnable() {
					@Override
					public void run() {
						underlyningDispatcher.onItemAdded(item, atIndex);
					}
				});
			} catch (Throwable e) {
				throw new RuntimeException("Faield to invoke handler on Edt thread", e);
			}
		}
	}

	@Override
	public void onItemChanged(final E item, final int atIndex) {
		if (SwingUtilities.isEventDispatchThread()) {
			underlyningDispatcher.onItemChanged(item, atIndex);
		} else {
			try {
				Edt.invokeOnEdtAndWait(new Runnable() {
					@Override
					public void run() {
						underlyningDispatcher.onItemChanged(item, atIndex);
					}
				});
			} catch (Throwable e) {
				throw new RuntimeException("Faield to invoke handler on Edt thread", e);
			}
		}
	}

	@Override
	public void onItemRemoved(final E item, final int wasAtIndex) {
		if (SwingUtilities.isEventDispatchThread()) {
			underlyningDispatcher.onItemRemoved(item, wasAtIndex);
		} else {
			try {
				Edt.invokeOnEdtAndWait(new Runnable() {
					@Override
					public void run() {
						underlyningDispatcher.onItemRemoved(item, wasAtIndex);
					}
				});
			} catch (Throwable e) {
				throw new RuntimeException("Faield to invoke handler on Edt thread", e);
			}
		}
	}

	@Override
	public void onAllItemsRemoved(final int sizeWas) {
		if (SwingUtilities.isEventDispatchThread()) {
			underlyningDispatcher.onAllItemsRemoved(sizeWas);
		} else {
			try {
				Edt.invokeOnEdtAndWait(new Runnable() {
					@Override
					public void run() {
						underlyningDispatcher.onAllItemsRemoved(sizeWas);
					}
				});
			} catch (Throwable e) {
				throw new RuntimeException("Faield to invoke handler on Edt thread", e);
			}
		}
	}
}
