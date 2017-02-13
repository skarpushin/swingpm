package ru.skarpushin.swingpm.tools.sglayout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.google.common.base.Preconditions;

/**
 * Simple but yet smart enough grid layout to not brake layout because of JLabel
 * and other components
 * 
 * @author sergey.karpushin
 * 
 */
public class SgLayout implements LayoutManager2 {
	private static Logger log = Logger.getLogger(SgLayout.class);

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

	private int getVGapTotalSize() {
		// we have gaps only between components
		return vgap * (rows - 1);
	}

	private int getHGapTotalSize() {
		return hgap * (cols - 1);
	}

	@Override
	public void removeLayoutComponent(Component comp) {
		synchronized (comp.getTreeLock()) {
			components.remove(comp);
		}
	}

	@Override
	public Dimension minimumLayoutSize(Container parent) {
		synchronized (parent.getTreeLock()) {
			Insets parentInsets = parent.getInsets();

			int[] colsSizesEffective = new int[cols];
			int[] rowsSizesEffective = new int[rows];
			calcConstColSizes(colsSizesEffective, minimumWidth);
			calcConstRowSizes(rowsSizesEffective, minimumHeight);

			Dimension ret = new Dimension(sum(colsSizesEffective, hgap) + parentInsets.left + parentInsets.right,
					sum(rowsSizesEffective, vgap) + parentInsets.top + parentInsets.bottom);

			return ret;
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

	@Override
	public Dimension preferredLayoutSize(Container parent) {
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
			calcConstRowSizes(rowsSizesEffective, preferredHeight);
			if (availWidth < sum(colsSizesEffective, hgap) || availHeight < sum(rowsSizesEffective, vgap)) {
				log.warn("Returning MINIMUM layout size");
				return minimumLayoutSize(parent);
			}
			return new Dimension(availWidth, availHeight);
		}
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
		int constHeight = calcConstRowSizes(rowsSizesEffective, preferredHeight);
		if (availHeight < sum(rowsSizesEffective, vgap)) {
			log.debug("AvailHeight less than poreferred. Returning min height");
			calcConstRowSizes(rowsSizesEffective, minimumHeight);
			return rowsSizesEffective;
		}
		calculateWeightedSizes(rowsSizes, rowsSizesTypes, availHeight - constHeight, rowsSizesEffective, vgap);
		return rowsSizesEffective;
	}

	private int[] calculateEffectiveColSizesForLayout(int availWidth) {
		int[] colsSizesEffective = new int[cols];
		int constWidth = calcConstColSizes(colsSizesEffective, preferredWidth);
		if (availWidth < sum(colsSizesEffective, hgap)) {
			log.debug("AvailWidth less than poreferred. Returning min width");
			calcConstRowSizes(colsSizesEffective, minimumWidth);
			return colsSizesEffective;
		}
		calculateWeightedSizes(colsSizes, colsSizesTypes, availWidth - constWidth, colsSizesEffective, hgap);
		updateComponentsWidths(colsSizesEffective);
		return colsSizesEffective;
	}

	private void positionComponents(Insets parentInsets, int[] colsSizesEffective, int[] rowsSizesEffective) {
		for (Entry<Component, SmartGridLayoutConstraints> entry : components.entrySet()) {
			SmartGridLayoutConstraints c = entry.getValue();

			int left = (c.col > 0 ? hgap : 0) + parentInsets.left;
			int top = (c.row > 0 ? vgap : 0) + parentInsets.top;
			for (int idxCol = 0; idxCol < c.col; idxCol++) {
				left += colsSizesEffective[idxCol];
			}
			for (int idxRow = 0; idxRow < c.row; idxRow++) {
				top += rowsSizesEffective[idxRow];
			}

			int width = 0;
			int height = 0;
			for (int idxCol = c.col; idxCol < c.colspan + c.col; idxCol++) {
				width += colsSizesEffective[idxCol];
			}
			if (c.col > 0) {
				width -= hgap;
			}

			for (int idxRow = c.row; idxRow < c.rowspan + c.row; idxRow++) {
				height += rowsSizesEffective[idxRow];
			}
			if (c.row > 0) {
				height -= vgap;
			}

			Component comp = entry.getKey();
			// comp.setSize(width, height);
			comp.setBounds(left, top, width, height);
		}
	}

	/**
	 * Calculate effective cols and rows sizes which hase dynamic sizes
	 * 
	 * @param sizes
	 * @param sizesTypes
	 * @param dynaSpace
	 *            space which is dynamically available and is to be distributed
	 *            between dynamically sized components
	 * @param gapSize
	 *            gap size for this direction
	 * @return
	 */
	private static void calculateWeightedSizes(int[] sizes, int[] sizesTypes, int dynaSpace, int[] dstArray,
			int gapSize) {
		double dynaSpaceD = (double) dynaSpace;
		double weightsSumm = 0;
		for (int i = 0; i < sizes.length; i++) {
			if (sizesTypes[i] == SIZE_TYPE_WEIGHTED) {
				weightsSumm += sizes[i];
			}
		}

		for (int i = 0; i < sizes.length; i++) {
			if (sizesTypes[i] == SIZE_TYPE_WEIGHTED) {
				dstArray[i] = (int) (dynaSpaceD * ((double) sizes[i]) / weightsSumm) + (i > 0 ? gapSize : 0);
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
			if (c.col > 0) {
				width -= hgap;
			}

			Component comp = entry.getKey();
			comp.setSize(width, comp.getHeight());
		}
	}

	/**
	 * Returns sum of non-weighted sizes
	 * 
	 * @param askComponent
	 *            what size to get when asking component
	 */
	private int calcConstRowSizes(int[] effectiveRowsSizesToPopulate, SizeResolver askComponent) {
		int height = getVGapTotalSize();
		for (int idxRow = 0; idxRow < rows; idxRow++) {
			if (rowsSizesTypes[idxRow] == SIZE_TYPE_CONSTANT) {
				height += rowsSizes[idxRow];
				effectiveRowsSizesToPopulate[idxRow] = rowsSizes[idxRow] + (idxRow > 0 ? vgap : 0);
			} else if (rowsSizesTypes[idxRow] == SIZE_TYPE_ASKCOMPONENT) {
				int addedHeight = findMaxHeightWithinRow(idxRow, askComponent);
				height += addedHeight;
				effectiveRowsSizesToPopulate[idxRow] = addedHeight + (idxRow > 0 ? vgap : 0);
			} else if (rowsSizesTypes[idxRow] == SIZE_TYPE_WEIGHTED) {
				int addedHeight = findMaxHeightWithinRow(idxRow, minimumHeight);
				effectiveRowsSizesToPopulate[idxRow] = addedHeight + (idxRow > 0 ? vgap : 0);
			}
		}
		return height;
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
	private int calcConstColSizes(int[] effectiveColsSizesToPopulate, SizeResolver askComponent) {
		int width = getHGapTotalSize();
		for (int idxCol = 0; idxCol < cols; idxCol++) {
			if (colsSizesTypes[idxCol] == SIZE_TYPE_CONSTANT) {
				width += colsSizes[idxCol];
				effectiveColsSizesToPopulate[idxCol] = colsSizes[idxCol] + (idxCol > 0 ? hgap : 0);
			} else if (colsSizesTypes[idxCol] == SIZE_TYPE_ASKCOMPONENT) {
				int addedWidth = findMaxWidthWithinColumn(idxCol, askComponent);
				width += addedWidth;
				effectiveColsSizesToPopulate[idxCol] = addedWidth + (idxCol > 0 ? hgap : 0);
			} else if (colsSizesTypes[idxCol] == SIZE_TYPE_WEIGHTED) {
				int addedWidth = findMaxWidthWithinColumn(idxCol, minimumWidth);
				effectiveColsSizesToPopulate[idxCol] = addedWidth + (idxCol > 0 ? hgap : 0);
			}
		}
		return width;
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
