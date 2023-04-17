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
package ru.skarpushin.swingpm.modelprops.virtualtable;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Responsible for tracking scroll and deciding whenever additional data needs
 * to be loaded to be displayed in the table
 */
public abstract class DataLoadingTriggerAbstract<E> {
	protected AsyncDataLoader<E> asyncDataLoader;

	protected DataLoadingTriggerAbstract(AsyncDataLoader<E> asyncDataLoader) {
		this.asyncDataLoader = asyncDataLoader;
		asyncDataLoader.setDataLoadingTriggerAbstract(this);
	}

	/**
	 * Impl must call it whenever new range of data displayed
	 */
	protected void triggerEnsureDataLoaded(int pageIdx) {
		asyncDataLoader.handlePageNeedsToBeLoaded(pageIdx);
	}

	public abstract int getHighPriorityRow();

	public AsyncDataLoader<E> getAsyncDataLoader() {
		return asyncDataLoader;
	}

	public void setAsyncDataLoader(AsyncDataLoader<E> asyncDataLoader) {
		this.asyncDataLoader = asyncDataLoader;
	}

	public void sort(List<HasPageIdx> loadPageTasks) {
		Collections.sort(loadPageTasks, new PagesComparator(getHighPriorityRow()));
	}

	private static class PagesComparator implements Comparator<HasPageIdx> {
		private int highPriorityRow;

		public PagesComparator(int highPriorityRow) {
			this.highPriorityRow = highPriorityRow;
		}

		@Override
		public int compare(HasPageIdx o1, HasPageIdx o2) {
			return Math.abs(o1.getPageIdx() - highPriorityRow) - Math.abs(o2.getPageIdx() - highPriorityRow);
		}
	}
}
