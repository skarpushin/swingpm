/*******************************************************************************
 * PGPTool is a desktop application for pgp encryption/decryption
 * Copyright 2015-2023 Sergey Karpushin
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 ******************************************************************************/
package ru.skarpushin.swingpm.bindings;

import javax.swing.Action;

/**
 * Class for convenience binding action trigger to action
 */
public class HasActionBinding implements Binding {
	private Action action;
	private final HasAction actionTrigger;

	public HasActionBinding(Action action, HasAction actionTrigger) {
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
