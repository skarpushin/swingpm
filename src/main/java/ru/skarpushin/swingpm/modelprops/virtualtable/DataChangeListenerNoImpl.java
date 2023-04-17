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
package ru.skarpushin.swingpm.modelprops.virtualtable;

/**
 * Responsible for handling situations when cached data must be invalidated or
 * just updated (for single row change).
 * 
 * WARNING: this impl doesn't actually track changes - it's just provide method
 * {@link #triggerRowChanged(Object)} and {@link #triggerRowCountChanged()} so
 * someone external (or subclass) will call it
 */
public class DataChangeListenerNoImpl<E> {
	protected AsyncDataLoader<E> asyncDataLoader;

	public DataChangeListenerNoImpl(AsyncDataLoader<E> asyncDataLoader) {
		this.asyncDataLoader = asyncDataLoader;
	}

	/**
	 * Impl must call it when one row change is detected.
	 * 
	 * @param row
	 *            new version of row which is eligible for display and was changed)
	 */
	public void triggerRowChanged(E row) {
		if (!asyncDataLoader.getVirtualTableDataSource().isSuitable(row)) {
			// NOTE: What if it was suitable but it's not after change?...
			int idx = asyncDataLoader.getModelVirtualTableProperty().getModelTablePropertyAccessor().indexOf(row);
			if (idx >= 0) {
				triggerRowCountChanged();
			}
			return;
		}
		asyncDataLoader.handleRowChanged(row);
	}

	public void triggerRowCountChanged() {
		asyncDataLoader.handleRowCountChanged();
	}

	public AsyncDataLoader<E> getAsyncDataLoader() {
		return asyncDataLoader;
	}

	public void setAsyncDataLoader(AsyncDataLoader<E> asyncDataLoader) {
		this.asyncDataLoader = asyncDataLoader;
	}

}
