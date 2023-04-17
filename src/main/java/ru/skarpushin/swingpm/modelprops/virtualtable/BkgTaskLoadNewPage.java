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

public class BkgTaskLoadNewPage<E> extends BkgTask<E> implements Runnable, HasPageIdx {

	protected int pageIdx;
	private PaginatedList<E> page;

	public BkgTaskLoadNewPage(AsyncDataLoaderImpl<E> loader, Object statusId, int pageIdx) {
		super(loader, statusId);
		this.pageIdx = pageIdx;
	}

	@Override
	public void perform() {
		page = loader.virtualTableDataSource.loadData(new PagerParams(pageIdx * loader.pageSize, loader.pageSize));
		loader.edtInvoker.invoke(this);
	}

	@Override
	public void run() {
		if (stateId != loader.currentStateId) {
			return;
		}
		loader.modelVirtualTableProperty.handleNewDataLoaded(page);
	}

	@Override
	public int getPageIdx() {
		return pageIdx;
	}

	@Override
	public String toString() {
		return "BkgTaskLoadNewPage [pageIdx=" + pageIdx + "]";
	}

}
