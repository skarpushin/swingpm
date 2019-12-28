package ru.skarpushin.swingpm.modelprops.virtualtable;

import org.apache.log4j.Logger;

public class RecurringBackgroundTask {
	private static Logger log = Logger.getLogger(RecurringBackgroundTask.class);

	private Runnable runnable;
	private long delayMs;
	private Thread thread;
	private boolean tearDownRequested;

	public RecurringBackgroundTask(Runnable runnable, long delayMs) {
		this.runnable = runnable;
		this.delayMs = delayMs;

		String threadName = chooseThreadName(runnable);
		thread = new Thread(envelop, threadName);
		thread.setDaemon(true);
		thread.start();
	}

	private String chooseThreadName(Runnable runnable) {
		String threadName = runnable.toString();
		threadName = threadName.substring(threadName.lastIndexOf(".") + 1);
		return threadName;
	}

	@SuppressWarnings("deprecation")
	public void tearDown(long joinTimeout) {
		tearDownRequested = true;
		thread.interrupt();
		try {
			thread.join(joinTimeout);
		} catch (InterruptedException e) {
			log.warn("Failed to gracefully tear down thread", e);
			thread.stop();
		}
	}

	private Runnable envelop = new Runnable() {
		@Override
		public void run() {
			try {
				while (!Thread.interrupted() || !tearDownRequested) {
					try {
						// do job itself
						runnable.run();
					} catch (Throwable t) {
						logSafe(t);
					}

					// delay
					try {
						Thread.sleep(delayMs);
					} catch (InterruptedException ie) {
						if (tearDownRequested) {
							return;
						} else {
							continue;
						}
					}
				}
			} finally {
				log.info("Envelope finished for " + runnable);
			}
		}

		private void logSafe(Throwable t) {
			try {
				log.error("Iteration failed for " + runnable, t);
			} catch (Throwable exc) {
				// do nothing
			}
		}
	};
}