package ru.skarpushin.swingpm.tools.edt;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

public class EdtInvokerSimpleImpl implements EdtInvoker {
	private static Logger log = Logger.getLogger(EdtInvokerSimpleImpl.class);

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
