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
package ru.skarpushin.swingpm.base;

public interface Detachable {

	/**
	 * Is attached to something outer?
	 * 
	 * @return true if there is something (normally event subsciption, or some timer
	 *         which periodically triggers some operation)
	 */
	boolean isAttached();

	/**
	 * Detach from any external dependency or stop any backgroung processing/working
	 */
	void detach();
}
