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
package ru.skarpushin.swingpm.tools.edt;

import javax.swing.SwingUtilities;

public class Edt {
	private static EdtInvoker edtInvoker = new EdtInvokerSimpleImpl();

	public static EdtInvoker getEdtInvoker() {
		return edtInvoker;
	}

	public static void getEdtInvoker(EdtInvoker newValue) {
		edtInvoker = newValue;
	}

	public static void invokeOnEdtAsync(Runnable runnable) {
		if (SwingUtilities.isEventDispatchThread()) {
			runnable.run();
		} else {
			SwingUtilities.invokeLater(runnable);
		}
	}

	public static void invokeOnEdtAndWait(Runnable runnable) {
		getEdtInvoker().invoke(runnable);
	}

}
