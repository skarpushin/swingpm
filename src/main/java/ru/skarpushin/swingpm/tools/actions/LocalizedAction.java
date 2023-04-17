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
package ru.skarpushin.swingpm.tools.actions;

import javax.swing.AbstractAction;
import javax.swing.Action;

import ru.skarpushin.swingpm.tools.SwingPmSettings;

public abstract class LocalizedAction extends AbstractAction {
	private static final long serialVersionUID = 5177364704498790332L;
	
	protected final String actionNameMessageCode;

	public LocalizedAction(String actionNameMessageCode) {
		this.actionNameMessageCode = actionNameMessageCode;
	}

	@Override
	public Object getValue(String key) {
		if (Action.NAME.equals(key)) {
			return SwingPmSettings.getMessages().get(actionNameMessageCode);
		}
		return super.getValue(key);
	};
}
