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
package ru.skarpushin.swingpm.valueadapters;

/**
 * 
 * @author sergeyk
 * 
 * @param <TInner>
 *            this most likely closer to POJO (DTO)
 * @param <TOuter>
 *            this most likely closer to PM/View
 */
public abstract class ConversionValueAdapter<TInner, TOuter> implements ValueAdapter<TOuter> {
	protected final ValueAdapter<TInner> innerValueAdapter;

	public ConversionValueAdapter(ValueAdapter<TInner> innerValueAdapter) {
		this.innerValueAdapter = innerValueAdapter;
	}

	@Override
	public TOuter getValue() {
		return convertInnerToOuter(innerValueAdapter.getValue());
	}

	@Override
	public void setValue(TOuter value) {
		innerValueAdapter.setValue(convertOuterToInner(value));
	}

	protected abstract TOuter convertInnerToOuter(TInner value);

	protected abstract TInner convertOuterToInner(TOuter value);
}
