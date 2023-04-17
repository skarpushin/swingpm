/*******************************************************************************
 * Copyright 2015-2023 Sergey Karpushin
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
package ru.skarpushin.swingpm.tools.sglayout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Preconditions;

/**
 * Simple but yet smart enough grid layout to not brake layout because of JLabel
 * and other components
 * 
 * @author sergey.karpushin
 * 
 */
public class SgLayout implements LayoutManager2 {
	private static Logger log = LogManager.getLogger(SgLayout.class);

	public static final int DEFAULT_ROW_HEIGHT = 14;
	public static final int DEFAULT_COL_WIDTH = 25;

	public static final int SIZE_TYPE_CONSTANT = 1;
	public static final int SIZE_TYPE_WEIGHTED = 2;
	public static final int SIZE_TYPE_ASKCOMPONENT = 3;

	private Map<Component, SmartGridLayoutConstraints> components = new HashMap<Component, SmartGridLayoutConstraints>();

	private final int rows;
	private final int cols;

	private int[] rowsSizes;
	private int[] colsSizes;
	private int[] rowsSizesTypes;
	private int[] colsSizesTypes;
	private final int hgap;
	private final int vgap;

	public SgLayout(int cols, int rows, int hgap, int vgap) {
		Preconditions.checkArgument(rows > 0, "rows must be > 0");
		Preconditions.checkArgument(cols > 0, "cols must be > 0");
		Preconditions.checkArgument(hgap >= 0, "hgap must be >= 0");
		Preconditions.checkArgument(vgap >= 0, "vgap must be >= 0");

		this.rows = rows;
		this.cols = cols;
		initSizeArrays();

		this.hgap = hgap;
		this.vgap = vgap;
	}

	public static SmartGridLayoutConstraints c(int col, int row) {
		return new SmartGridLayoutConstraints(col, row);
	}

	public static SmartGridLayoutConstraints c(int col, int row, int colspan, int rowspan) {
		return new SmartGridLayoutConstraints(col, row, colspan, rowspan);
	}

	public SmartGridLayoutConstraints cs(int col, int row) {
		return new SmartGridLayoutConstraints(col, row);
	}

	public SmartGridLayoutConstraints cs(int col, int row, int colspan, int rowspan) {
		return new SmartGridLayoutConstraints(col, row, colspan, rowspan);
	}

	private void initSizeArrays() {
		rowsSizes = new int[rows];
		colsSizes = new int[cols];
		rowsSizesTypes = new int[rows];
		colsSizesTypes = new int[cols];

		for (int i = 0; i < rows; i++) {
			rowsSizes[i] = DEFAULT_ROW_HEIGHT;
			rowsSizesTypes[i] = SIZE_TYPE_ASKCOMPONENT;
		}

		for (int i = 0; i < cols; i++) {
			colsSizes[i] = DEFAULT_COL_WIDTH;
			colsSizesTypes[i] = SIZE_TYPE_WEIGHTED;
		}
	}

	public void setRowSize(int row, int size, int sizeType) {
		Preconditions.checkArgument(0 <= row && row < rows, "Invalid row idx");
		Preconditions.checkArgument(size > 0, "Invalid size");
		Preconditions.checkArgument(SIZE_TYPE_CONSTANT <= sizeType && sizeType <= SIZE_TYPE_ASKCOMPONENT,
				"Invalid size type");

		rowsSizes[row] = size;
		rowsSizesTypes[row] = sizeType;
	}

	public void setColSize(int col, int size, int sizeType) {
		Preconditions.checkArgument(0 <= col && col < cols, "Invalid col idx");
		Preconditions.checkArgument(size > 0, "Invalid size");
		Preconditions.checkArgument(SIZE_TYPE_CONSTANT <= sizeType && sizeType <= SIZE_TYPE_ASKCOMPONENT,
				"Invalid size type");

		colsSizes[col] = size;
		colsSizesTypes[col] = sizeType;
	}

	@Override
	public void addLayoutComponent(String name, Component comp) {
		log.error("Operation NOT supported");
		Preconditions.checkState(false, "Operation not supported");
	}

	@Override
	public void addLayoutComponent(Component comp, Object constraints) {
		synchronized (comp.getTreeLock()) {
			Preconditions.checkArgument(comp != null, "Component must not be null");
			Preconditions.checkArgument(constraints != null && constraints instanceof SmartGridLayoutConstraints,
					"Constraints must not be bnull and instanceof SmartGridLayoutConstraints");

			SmartGridLayoutConstraints c = (SmartGridLayoutConstraints) constraints;

			Preconditions.checkArgument(c.col + c.colspan - 1 < cols,
					"Coordinates intersects with grid bounds by cols");
			Preconditions.checkArgument(c.row + c.rowspan - 1 < rows,
					"Coordinates intersects with grid bounds by rows");

			Component offending = findComponentWithinBounds(c);
			Preconditions.checkArgument(offending == null,
					"can't place new component to intersect with existing one: " + offending);

			// Add component to map
			components.put(comp, c);
		}
	}

	private Component findComponentWithinBounds(SmartGridLayoutConstraints c1) {
		Rectangle r1 = new Rectangle(c1.col, c1.row, c1.colspan, c1.rowspan);

		for (Entry<Component, SmartGridLayoutConstraints> entry : components.entrySet()) {
			SmartGridLayoutConstraints c2 = entry.getValue();
			Rectangle r2 = new Rectangle(c2.col, c2.row, c2.colspan, c2.rowspan);

			if (r1.intersects(r2)) {
				log.warn("Intersection for components: new " + r1 + " and existing " + r2);
				return entry.getKey();
			}
		}

		return null;
	}

	private Component findComponentAt(int col, int row) {
		for (Entry<Component, SmartGridLayoutConstraints> entry : components.entrySet()) {
			SmartGridLayoutConstraints c = entry.getValue();
			if ((c.col <= col && col <= c.col + c.colspan - 1) && (c.row <= row && row <= c.row + c.rowspan - 1)) {
				return entry.getKey();
			}
		}

		return null;
	}

	@Override
	public void removeLayoutComponent(Component comp) {
		synchronized (comp.getTreeLock()) {
			components.remove(comp);
		}
	}

	@Override
	public Dimension minimumLayoutSize(Container parent) {
		log.debug("ENTERING minimumLayoutSize");

		synchronized (parent.getTreeLock()) {
			Insets parentInsets = parent.getInsets();

			int[] colsSizesEffective = new int[cols];
			int[] rowsSizesEffective = new int[rows];
			calcConstColSizes(colsSizesEffective, minimumWidth);
			calcConstRowSizes(rowsSizesEffective, minimumHeight);

			Dimension ret = new Dimension(sum(colsSizesEffective, hgap), sum(rowsSizesEffective, vgap));

			if (log.isDebugEnabled()) {
				log.debug("RETURN minimumLayoutSize " + ret);
			}
			return add(ret, parentInsets);
		}
	}

	private int sum(int[] ints, int gap) {
		int ret = 0;
		for (int i : ints) {
			ret += i;
		}
		if (gap > 0) {
			ret += gap * (ints.length - 1);
		}
		return ret;
	}

	private int sumNonWeighted(int[] ints, int gap, int[] sizeTypes) {
		int ret = 0;
		for (int i = 0; i < ints.length; i++) {
			if (sizeTypes[i] == SIZE_TYPE_WEIGHTED) {
				continue;
			}
			ret += ints[i];
		}
		if (gap > 0) {
			ret += gap * (ints.length - 1);
		}
		return ret;
	}

	@Override
	public Dimension preferredLayoutSize(Container parent) {
		log.debug("\n\n\nENTERING preferredLayoutSize");

		synchronized (parent.getTreeLock()) {
			// See how much space we have for our rendering
			Insets parentInsets = parent.getInsets();
			int availWidth = parent.getWidth() - (parentInsets.left + parentInsets.right);
			int availHeight = parent.getHeight() - (parentInsets.top + parentInsets.bottom);

			// See how much space is for constantly sized components and which
			// for dynamically
			int[] colsSizesEffective = new int[cols];
			int[] rowsSizesEffective = new int[rows];
			calcConstColSizes(colsSizesEffective, preferredWidth);
			updateComponentsWidths(colsSizesEffective);
			calcConstRowSizes(rowsSizesEffective, preferredHeight);
			int prefWidth = sum(colsSizesEffective, hgap);
			int prefHeight = sum(rowsSizesEffective, vgap);
			if (availWidth < prefWidth || availHeight < prefHeight) {
				if (log.isDebugEnabled()) {
					log.debug("Available size " + availWidth + " x " + availHeight + " is LESS than preferred: "
							+ prefWidth + " x " + prefHeight);
				}
				// return minimumLayoutSize(parent);
			}
			Dimension ret = new Dimension(prefWidth, prefHeight);
			if (log.isDebugEnabled()) {
				log.debug("RETURN preferredLayoutSize: " + ret);
			}
			return add(ret, parentInsets);
		}
	}

	private Dimension add(Dimension ret, Insets ins) {
		return new Dimension(ret.width + ins.left + ins.right, ret.height + ins.top + ins.bottom);
	}

	@Override
	public Dimension maximumLayoutSize(Container target) {
		return new Dimension(10000, 10000);
	}

	@Override
	public void invalidateLayout(Container target) {
		// do nothing, not needed to be implemented
	}

	@Override
	public void layoutContainer(Container parent) {
		log.debug("\n\n\nENTERING layoutContainer");
		synchronized (parent.getTreeLock()) {
			// See how much space we have for our rendering
			Insets parentInsets = parent.getInsets();

			// Process columns sizes
			int availWidth = parent.getWidth() - (parentInsets.left + parentInsets.right);
			int[] colsSizesEffective = calculateEffectiveColSizesForLayout(availWidth);
			if (colsSizesEffective == null) {
				return;
			}

			// Process rows sizes
			int availHeight = parent.getHeight() - (parentInsets.top + parentInsets.bottom);
			int[] rowsSizesEffective = calculateEffectiveRowsSizesForLayout(availHeight);
			if (rowsSizesEffective == null) {
				return;
			}

			// Place controls to their places
			positionComponents(parentInsets, colsSizesEffective, rowsSizesEffective);
		}
	}

	private int[] calculateEffectiveRowsSizesForLayout(int availHeight) {
		int[] rowsSizesEffective = new int[rows];
		calcConstRowSizes(rowsSizesEffective, preferredHeight);
		int minPreferred = sum(rowsSizesEffective, vgap);
		if (availHeight < minPreferred) {
			if (log.isDebugEnabled()) {
				log.debug("AvailHeight " + availHeight + " less than preferred " + minPreferred);
			}
			// calcConstRowSizes(rowsSizesEffective, minimumHeight);
			return rowsSizesEffective;
		}
		int constHeight = sumNonWeighted(rowsSizesEffective, vgap, rowsSizesTypes);
		calculateWeightedSizes(rowsSizes, rowsSizesTypes, availHeight - constHeight, rowsSizesEffective);
		return rowsSizesEffective;
	}

	private int[] calculateEffectiveColSizesForLayout(int availWidth) {
		int[] colsSizesEffective = new int[cols];
		calcConstColSizes(colsSizesEffective, preferredWidth);
		int minPreferred = sum(colsSizesEffective, hgap);
		if (availWidth < minPreferred) {
			if (log.isDebugEnabled()) {
				log.debug("AvailWidth " + availWidth + " less than preferred " + minPreferred);
			}
			// calcConstColSizes(colsSizesEffective, minimumWidth);
			return colsSizesEffective;
		}
		int constWidth = sumNonWeighted(colsSizesEffective, hgap, colsSizesTypes);
		calculateWeightedSizes(colsSizes, colsSizesTypes, availWidth - constWidth, colsSizesEffective);
		updateComponentsWidths(colsSizesEffective);
		return colsSizesEffective;
	}

	private void positionComponents(Insets parentInsets, int[] colsSizesEffective, int[] rowsSizesEffective) {
		for (Entry<Component, SmartGridLayoutConstraints> entry : components.entrySet()) {
			SmartGridLayoutConstraints c = entry.getValue();

			int left = parentInsets.left;
			for (int idxCol = 0; idxCol < c.col; idxCol++) {
				left += colsSizesEffective[idxCol];
				left += hgap;
			}

			int top = parentInsets.top;
			for (int idxRow = 0; idxRow < c.row; idxRow++) {
				top += rowsSizesEffective[idxRow];
				top += vgap;
			}

			int width = 0;
			for (int idxCol = c.col; idxCol < c.colspan + c.col; idxCol++) {
				width += colsSizesEffective[idxCol];
				if (idxCol > c.col) {
					width += hgap;
				}
			}

			int height = 0;
			for (int idxRow = c.row; idxRow < c.rowspan + c.row; idxRow++) {
				height += rowsSizesEffective[idxRow];
				if (idxRow > c.row) {
					height += vgap;
				}
			}

			Component comp = entry.getKey();
			comp.setBounds(left, top, width, height);
		}
	}

	/**
	 * Calculate effective cols and rows sizes which hase dynamic sizes
	 * 
	 * @param dynaSpace
	 *            space which is dynamically available and is to be distributed
	 *            between dynamically sized components
	 */
	private static void calculateWeightedSizes(int[] sizes, int[] sizesTypes, int dynaSpace, int[] dstArray) {
		double dynaSpaceD = (double) dynaSpace;
		double weightsSumm = 0;
		for (int i = 0; i < sizes.length; i++) {
			if (sizesTypes[i] == SIZE_TYPE_WEIGHTED) {
				weightsSumm += sizes[i];
			}
		}

		for (int i = 0; i < sizes.length; i++) {
			if (sizesTypes[i] == SIZE_TYPE_WEIGHTED) {
				dstArray[i] = (int) (dynaSpaceD * ((double) sizes[i]) / weightsSumm);
			}
		}
	}

	private void updateComponentsWidths(int[] colsSizesEffective) {
		for (Entry<Component, SmartGridLayoutConstraints> entry : components.entrySet()) {
			SmartGridLayoutConstraints c = entry.getValue();

			int width = 0;
			for (int idxCol = c.col; idxCol < c.colspan + c.col; idxCol++) {
				width += colsSizesEffective[idxCol];
			}

			Component comp = entry.getKey();
			if (log.isDebugEnabled()) {
				log.debug("Component: " + comp);
				log.debug("\tCurrent size: " + comp.getSize());
				log.debug("\tCurrent pref size: " + comp.getPreferredSize());
			}
			// comp.setSize(width, comp.getHeight());
			Rectangle bounds = comp.getBounds();
			Rectangle newBounds = new Rectangle(bounds.x, bounds.y, width, comp.getHeight());
			if (!bounds.equals(newBounds)) {
				if (log.isDebugEnabled()) {
					log.debug("\tRequested width: " + newBounds.width);
				}

				comp.setBounds(newBounds);

				if (log.isDebugEnabled()) {
					log.debug("\tNew size: " + comp.getSize());
					log.debug("\tNew pref size: " + comp.getPreferredSize());
				}
			}
		}
	}

	/**
	 * Returns sum of non-weighted sizes
	 * 
	 * @param askComponent
	 *            what size to get when asking component
	 */
	private void calcConstRowSizes(int[] effectiveRowsSizesToPopulate, SizeResolver askComponent) {
		for (int idxRow = 0; idxRow < rows; idxRow++) {
			if (rowsSizesTypes[idxRow] == SIZE_TYPE_CONSTANT) {
				effectiveRowsSizesToPopulate[idxRow] = rowsSizes[idxRow];
			} else if (rowsSizesTypes[idxRow] == SIZE_TYPE_ASKCOMPONENT) {
				int addedHeight = findMaxHeightWithinRow(idxRow, askComponent);
				effectiveRowsSizesToPopulate[idxRow] = addedHeight;
			} else if (rowsSizesTypes[idxRow] == SIZE_TYPE_WEIGHTED) {
				int addedHeight = findMaxHeightWithinRow(idxRow, minimumHeight);
				effectiveRowsSizesToPopulate[idxRow] = addedHeight;
			}
		}
		if (log.isDebugEnabled()) {
			log.debug("calcConstRowSizes " + Arrays.toString(effectiveRowsSizesToPopulate));
		}
	}

	private int findMaxHeightWithinRow(int idxRow, SizeResolver sizeResolver) {
		int addedHeight = 0;
		for (int idxCol = 0; idxCol < cols; idxCol++) {
			Component comp = findComponentAt(idxCol, idxRow);
			if (comp == null) {
				continue;
			}
			SmartGridLayoutConstraints c = components.get(comp);
			if (c.rowspan > 1) {
				continue;
			}
			int compHeight = sizeResolver.get(comp);
			if (compHeight > addedHeight) {
				addedHeight = compHeight;
			}
		}
		if (addedHeight == 0) {
			log.warn("findMaxHeightWithinRow(), rowIdx " + idxRow + " :: addedHeight is 0, which is not good");
		}
		return addedHeight;
	}

	/**
	 * Returns sum of non-weighted sizes
	 * 
	 * @param askComponent
	 *            what size to get when asking component
	 */
	private void calcConstColSizes(int[] effectiveColsSizesToPopulate, SizeResolver askComponent) {
		for (int idxCol = 0; idxCol < cols; idxCol++) {
			if (colsSizesTypes[idxCol] == SIZE_TYPE_CONSTANT) {
				effectiveColsSizesToPopulate[idxCol] = colsSizes[idxCol];
			} else if (colsSizesTypes[idxCol] == SIZE_TYPE_ASKCOMPONENT) {
				int addedWidth = findMaxWidthWithinColumn(idxCol, askComponent);
				effectiveColsSizesToPopulate[idxCol] = addedWidth;
			} else if (colsSizesTypes[idxCol] == SIZE_TYPE_WEIGHTED) {
				int addedWidth = findMaxWidthWithinColumn(idxCol, minimumWidth);
				effectiveColsSizesToPopulate[idxCol] = addedWidth;
			}
		}
		if (log.isDebugEnabled()) {
			log.debug("calcConstColSizes " + Arrays.toString(effectiveColsSizesToPopulate));
		}
	}

	private int findMaxWidthWithinColumn(int idxCol, SizeResolver sizeResolver) {
		int addedWidth = 0;
		for (int idxRow = 0; idxRow < rows; idxRow++) {
			Component comp = findComponentAt(idxCol, idxRow);
			if (comp == null) {
				continue;
			}
			SmartGridLayoutConstraints c = components.get(comp);
			if (c.colspan > 1) {
				continue;
			}
			int compWidth = sizeResolver.get(comp);
			if (compWidth > addedWidth) {
				addedWidth = compWidth;
			}
		}
		if (addedWidth == 0) {
			log.warn("findMaxWidthWithinColumn(), colIdx " + idxCol + " :: addedWidth is 0, which is not good");
		}
		return addedWidth;
	}

	@Override
	public float getLayoutAlignmentX(Container target) {
		return 0.5f;
	}

	@Override
	public float getLayoutAlignmentY(Container target) {
		return 0.5f;
	}

	public static interface SizeResolver {
		int get(Component c);
	}

	public static SizeResolver preferredWidth = new SizeResolver() {
		@Override
		public int get(Component c) {
			return c.getPreferredSize().width;
		}
	};

	public static SizeResolver preferredHeight = new SizeResolver() {
		@Override
		public int get(Component c) {
			return c.getPreferredSize().height;
		}
	};

	public static SizeResolver minimumWidth = new SizeResolver() {
		@Override
		public int get(Component c) {
			return c.getMinimumSize().width;
		}
	};

	public static SizeResolver minimumHeight = new SizeResolver() {
		@Override
		public int get(Component c) {
			return c.getMinimumSize().height;
		}
	};
}
