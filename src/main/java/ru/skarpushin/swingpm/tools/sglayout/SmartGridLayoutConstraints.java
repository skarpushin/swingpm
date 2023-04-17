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
package ru.skarpushin.swingpm.tools.sglayout;

import com.google.common.base.Preconditions;

public class SmartGridLayoutConstraints {
	protected int row;
	protected int col;
	protected int colspan;
	protected int rowspan;

	public SmartGridLayoutConstraints(int col, int row) {
		this(col, row, 1, 1);
	}

	public SmartGridLayoutConstraints(int col, int row, int colspan, int rowspan) {
		Preconditions.checkArgument(col >= 0);
		Preconditions.checkArgument(row >= 0);
		Preconditions.checkArgument(1 <= colspan);
		Preconditions.checkArgument(1 <= rowspan);

		this.row = row;
		this.col = col;
		this.rowspan = rowspan;
		this.colspan = colspan;
	}
}
