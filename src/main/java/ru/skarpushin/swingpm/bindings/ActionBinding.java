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
package ru.skarpushin.swingpm.bindings;

import javax.swing.AbstractButton;
import javax.swing.Action;

/**
 * Class for convenience binding action trigger to action
 */
public class ActionBinding implements Binding {
	private Action action;
	private final AbstractButton actionTrigger;

	public ActionBinding(Action action, AbstractButton actionTrigger) {
		this.action = action;
		this.actionTrigger = actionTrigger;

		actionTrigger.setAction(action);
	}

	@Override
	public boolean isBound() {
		return action != null;
	}

	@Override
	public void unbind() {
		action = null;
		actionTrigger.setAction(action);
	}
}
