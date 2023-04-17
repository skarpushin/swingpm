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

import ru.skarpushin.swingpm.tools.edt.Edt;
import ru.skarpushin.swingpm.tools.edt.EdtInvoker;

/**
 * Responsible for async data loading and updating model with it (have to be in
 * sync with what {@link DataChangeListenerNoImpl} does)
 */
public abstract class AsyncDataLoader<E> {
	protected ModelVirtualTableProperty<E> modelVirtualTableProperty;
	protected VirtualTableDataSource<E> virtualTableDataSource;
	protected EdtInvoker edtInvoker = Edt.getEdtInvoker();
	protected DataLoadingTriggerAbstract<E> dataLoadingTriggerAbstract;

	protected final int pageSize;

	protected AsyncDataLoader(ModelVirtualTableProperty<E> modelVirtualTableProperty,
			VirtualTableDataSource<E> virtualTableDataSource, int pageSize) {
		this.modelVirtualTableProperty = modelVirtualTableProperty;
		this.virtualTableDataSource = virtualTableDataSource;
		this.pageSize = pageSize;
	}

	/**
	 * Impl must rar-down any background things it might have
	 */
	public void tearDown() {
	}

	/**
	 * IMPORTANT: Assuming this will be called on EDT
	 */
	protected abstract void handleRowCountChanged();

	/**
	 * IMPORTANT: Assuming this will be called on EDT
	 */
	protected abstract void handleRowChanged(E row);

	/**
	 * For IMPL: might be called several times for same page
	 */
	protected abstract void handlePageNeedsToBeLoaded(int pageIdx);

	public ModelVirtualTableProperty<E> getModelVirtualTableProperty() {
		return modelVirtualTableProperty;
	}

	public void setModelVirtualTableProperty(ModelVirtualTableProperty<E> modelVirtualTableProperty) {
		this.modelVirtualTableProperty = modelVirtualTableProperty;
	}

	public VirtualTableDataSource<E> getVirtualTableDataSource() {
		return virtualTableDataSource;
	}

	public void setVirtualTableDataSource(VirtualTableDataSource<E> virtualTableDataSource) {
		this.virtualTableDataSource = virtualTableDataSource;
	}

	public DataLoadingTriggerAbstract<E> getDataLoadingTriggerAbstract() {
		return dataLoadingTriggerAbstract;
	}

	public void setDataLoadingTriggerAbstract(DataLoadingTriggerAbstract<E> dataLoadingTriggerAbstract) {
		this.dataLoadingTriggerAbstract = dataLoadingTriggerAbstract;
	}

}
