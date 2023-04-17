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
package ru.skarpushin.swingpm.valueadapters;

/**
 * Impl can define the way how value is set or retrieved. It might be mapped to
 * POJO using reflection or it might hold actuall value
 * 
 * @author sergeyk
 * 
 * @param <E>
 */
public interface ValueAdapter<E> {
	E getValue();

	void setValue(E value);
}
