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
 * Adapter which actually holds value but reuses to change it
 * 
 * @author sergeyk
 * 
 * @param <E>
 */
public class ValueAdapterReadonlyImpl<E> implements ValueAdapter<E> {
	private E value;

	public ValueAdapterReadonlyImpl() {
	}

	public ValueAdapterReadonlyImpl(E initialValue) {
		this.value = initialValue;
	}

	public static <E> ValueAdapterReadonlyImpl<E> build(E value) {
		return new ValueAdapterReadonlyImpl<E>(value);
	}

	@Override
	public E getValue() {
		return value;
	}

	@Override
	public void setValue(E value) {
		throw new IllegalStateException("Operation not supported");
	}

}
