package ru.skarpushin.swingpm.base;

import java.awt.Window;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PresentationModelBase implements PresentationModel {
	protected List<View<?>> views = new ArrayList<View<?>>();

	@Override
	public boolean isAttached() {
		return false;
	}

	@Override
	public void detach() {

	}

	@Override
	public void registerView(View<?> view) {
		views.add(view);
	}

	@Override
	public void unregisterView(View<?> view) {
		views.remove(view);
	}

	/**
	 * Search registered views for registered Window. Normally this is will be
	 * used as a parent for modal dialog
	 * 
	 * @return
	 */
	protected Window findRegisteredWindowIfAny() {
		for (View<?> view : views) {
			if (view instanceof HasWindow) {
				return ((HasWindow) view).getWindow();
			}
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	protected <T> List<T> findViewsWhichImplements(Class<T> clazz) {
		List<T> ret = new LinkedList<T>();

		for (View<?> view : views) {
			if (clazz.isAssignableFrom(view.getClass())) {
				ret.add((T) view);
			}
		}

		return ret;
	}
}
