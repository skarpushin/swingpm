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

/**
 * Impl which will not cause dead locks as described here:
 * http://www.jroller.com/tackline/entry/detecting_invokeandwait_abuse
 * 
 * @author sergey.karpushin
 * 
 */
public class EdtInvokerFixedImpl implements EdtInvoker {
	private static Logger log = LogManager.getLogger(EdtInvokerFixedImpl.class);
	private volatile int invokers = 0;

	@Override
	public void invoke(Runnable task) {
		if (SwingUtilities.isEventDispatchThread()) {
			task.run();
			return;
		}

		try {
			invokers++;
			new BlockingInvocationOnEdtThread(task);
		} finally {
			invokers--;
		}
	}

	private class BlockingInvocationOnEdtThread implements Runnable {
		private static final long DEAD_LOCK_LONG = 10 * 1000;

		private final Runnable task;
		private volatile boolean executionCompleted = false;
		private Throwable exceptionHappened;
		private long startedAt;

		public BlockingInvocationOnEdtThread(Runnable task) {
			this.task = task;

			SwingUtilities.invokeLater(this);

			startedAt = System.currentTimeMillis();
			while (!executionCompleted) {
				Thread.yield();

				if (System.currentTimeMillis() - startedAt > DEAD_LOCK_LONG) {
					log.error("Deadlock detected. Invokers: " + invokers, new RuntimeException("Deadlock detected"));
					System.exit(-2);
					return;
				}
			}

			if (exceptionHappened != null) {
				throw new RuntimeException("Unexpected exception happened", exceptionHappened);
			}
		}

		@Override
		public void run() {
			try {
				// log.debug("Executing task: " + task);
				task.run();
			} catch (Throwable t) {
				exceptionHappened = t;
			} finally {
				// log.debug("Executing task done: " + task);
				executionCompleted = true;
			}
		}
	}

}
