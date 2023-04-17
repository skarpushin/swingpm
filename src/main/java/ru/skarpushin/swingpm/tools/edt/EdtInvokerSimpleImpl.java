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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EdtInvokerSimpleImpl implements EdtInvoker {
	private static Logger log = LogManager.getLogger(EdtInvokerSimpleImpl.class);

	@Override
	public void invoke(Runnable task) {
		if (SwingUtilities.isEventDispatchThread()) {
			task.run();
			return;
		}

		try {
			SwingUtilities.invokeAndWait(task);
		} catch (InterruptedException ie) {
			log.trace("catch InterruptedException, considered as non-critical case, no exception being propogated", ie);
		} catch (Throwable t) {
			throw new RuntimeException("Failed to execute task on EDT", t);
		}
	}
}
