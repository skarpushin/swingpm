package ru.skarpushin.swingpm.tools.sglayout;

import com.google.common.base.Preconditions;

public class SmartGridLayoutConstraints {
	protected int row;
	protected int col;
	protected int colspan;
	protected int rowspan;

	public SmartGridLayoutConstraints(int col, int row) {
		this(col, row, 1, 1);
	}

	public SmartGridLayoutConstraints(int col, int row, int colspan, int rowspan) {
		Preconditions.checkArgument(col >= 0);
		Preconditions.checkArgument(row >= 0);
		Preconditions.checkArgument(1 <= colspan);
		Preconditions.checkArgument(1 <= rowspan);

		this.row = row;
		this.col = col;
		this.rowspan = rowspan;
		this.colspan = colspan;
	}
}
