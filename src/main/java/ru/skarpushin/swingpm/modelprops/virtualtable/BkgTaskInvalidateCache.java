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

import org.summerb.easycrud.api.dto.PagerParams;
import org.summerb.easycrud.api.dto.PaginatedList;

public class BkgTaskInvalidateCache<E> extends BkgTask<E> implements Runnable {
	protected int pageIdx;
	private PaginatedList<E> firstPage;
	private PaginatedList<E> currentPage;

	public BkgTaskInvalidateCache(AsyncDataLoaderImpl<E> loader, Object statusId) {
		super(loader, statusId);
	}

	@Override
	public void perform() {
		// do load data
		firstPage = loader.virtualTableDataSource.loadData(new PagerParams(0, loader.pageSize));
		currentPage = null;
		if (loader.lastPageRequested > 0 && firstPage.getTotalResults() > loader.lastPageRequested * loader.pageSize) {
			currentPage = loader.virtualTableDataSource
					.loadData(new PagerParams(loader.lastPageRequested * loader.pageSize, loader.pageSize));
		} else {
			loader.lastPageRequested = -1;
		}

		// do update table with it
		loader.edtInvoker.invoke(this);

	}

	@Override
	public void run() {
		if (stateId != loader.currentStateId) {
			return;
		}
		loader.modelVirtualTableProperty.replaceCurrentDataWith(firstPage, currentPage);
	}

}
