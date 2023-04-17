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

import java.awt.Container;

public interface View<TPM> extends Detachable {
	void setPm(TPM pm);

	TPM getPm();

	/**
	 * Render to specific container
	 * 
	 * @param target
	 *            assuming it's null, Containrt or Window or Frame
	 * @param constraints
	 *            constaraints for case when view is a component added to panel or
	 *            other component
	 */
	void renderTo(Container target, Object constraints);

	void renderTo(Container target);

	void unrender();

}
