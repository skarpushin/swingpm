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

/**
 * Table might call this to notify if it didn't find data for row
 */
public interface RowRetrieverFeedbackHandler {
	/**
	 * NOTE for Impl: It must be blazing fast since this method called while table
	 * requesting for new data to be rendered
	 */
	void handleRowRequested(int rowIndex, boolean isDataFound);
}
