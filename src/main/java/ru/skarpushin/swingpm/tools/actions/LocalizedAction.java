package ru.skarpushin.swingpm.tools.actions;

import javax.swing.AbstractAction;
import javax.swing.Action;

import ru.skarpushin.swingpm.tools.SwingPmSettings;

public abstract class LocalizedAction extends AbstractAction {
	private static final long serialVersionUID = 5177364704498790332L;
	private final String actionNameMessageCode;

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
