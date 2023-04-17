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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DataLoadingTriggerFeedbackBasedImpl<E> extends DataLoadingTriggerAbstract<E>
		implements RowRetrieverFeedbackHandler {
	private static Logger log = LogManager.getLogger(DataLoadingTriggerFeedbackBasedImpl.class);

	private final int pageSize;

	private int lastRenderedRow = -1;

	public DataLoadingTriggerFeedbackBasedImpl(AsyncDataLoader<E> asyncDataLoader) {
		super(asyncDataLoader);
		pageSize = asyncDataLoader.pageSize;
	}

	@Override
	public void handleRowRequested(int rowIndex, boolean isDataFound) {
		lastRenderedRow = rowIndex;
		if (isDataFound) {
			return;
		}

		log.debug("row not found: " + rowIndex);

		int rowPage = rowIndex / pageSize;
		asyncDataLoader.handlePageNeedsToBeLoaded(rowPage);
	}

	@Override
	public int getHighPriorityRow() {
		return lastRenderedRow;
	}

}
