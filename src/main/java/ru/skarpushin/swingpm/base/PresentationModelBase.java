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
	 * Search registered views for registered Window. Normally this is will be used
	 * as a parent for modal dialog
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
