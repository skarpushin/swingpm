package ru.skarpushin.swingpm.modelprops.virtualtable;

import org.apache.log4j.Logger;

public class DataLoadingTriggerFeedbackBasedImpl<E> extends DataLoadingTriggerAbstract<E>
		implements RowRetrieverFeedbackHandler {
	private static Logger log = Logger.getLogger(DataLoadingTriggerFeedbackBasedImpl.class);

	private final int pageSize;

	private int lastRenderedRow = -1;

	public DataLoadingTriggerFeedbackBasedImpl(AsyncDataLoader<E> asyncDataLoader) {
		super(asyncDataLoader);
		pageSize = asyncDataLoader.pageSize;
	}

	@Override
	public void handleRowRequested(int rowIndex, boolean isDataFound) {
		lastRenderedRow = rowIndex;
		if (isDataFound) {
			return;
		}

		log.debug("row not found: " + rowIndex);

		int rowPage = rowIndex / pageSize;
		asyncDataLoader.handlePageNeedsToBeLoaded(rowPage);
	}

	@Override
	public int getHighPriorityRow() {
		return lastRenderedRow;
	}

}
