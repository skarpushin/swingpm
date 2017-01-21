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
		Preconditions.checkState(false, "Operation not supported");
	}

	@Override
	public void addLayoutComponent(Component comp, Object constraints) {
		Preconditions.checkArgument(comp != null, "Component must not be null");
		Preconditions.checkArgument(constraints != null && constraints instanceof SmartGridLayoutConstraints,
				"Constraints must not be bnull and instanceof SmartGridLayoutConstraints");

		SmartGridLayoutConstraints c = (SmartGridLayoutConstraints) constraints;

		Preconditions.checkArgument(c.col + c.colspan - 1 < cols, "Coordinates intersects with grid bounds by cols");
		Preconditions.checkArgument(c.row + c.rowspan - 1 < rows, "Coordinates intersects with grid bounds by rows");

		Component offending = findComponentWithinBounds(c);
		Preconditions.checkArgument(offending == null,
				"can't place new component to intersect with existing one: " + offending);

		// Add component to map
		components.put(comp, c);
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

	/**
	 * Defines the way how we ask component for it's size
	 */
	private Dimension getComponentPreferredSize(Component comp) {
		return comp.getPreferredSize();
	}

	@Override
	public void removeLayoutComponent(Component comp) {
		components.remove(comp);
	}

	@Override
	public Dimension minimumLayoutSize(Container parent) {
		synchronized (parent.getTreeLock()) {
			Insets parentInsets = parent.getInsets();

			int[] colsSizesEffective = new int[cols];
			int[] rowsSizesEffective = new int[rows];
			Dimension reqConstSize = calculateEffectiveSizes1stPass(colsSizesEffective, rowsSizesEffective);
			int weightedColumnCount = getWeightedSizeCount(colsSizesTypes);
			int weightedRowCount = getWeightedSizeCount(rowsSizesTypes);

			Dimension ret = new Dimension(
					reqConstSize.width + weightedColumnCount * DEFAULT_COL_WIDTH + parentInsets.left
							+ parentInsets.right,
					reqConstSize.height + weightedRowCount * DEFAULT_ROW_HEIGHT + parentInsets.top
							+ parentInsets.bottom);

			// if (log.isDebugEnabled()) {
			// log.debug("minimumLayoutSize()" + ret);
			// }

			return ret;
		}
	}

	private int getWeightedSizeCount(int[] sizesTypes) {
		int ret = 0;
		for (int i = 0; i < sizesTypes.length; i++) {
			if (sizesTypes[i] == SIZE_TYPE_WEIGHTED) {
				ret++;
			}
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
			Dimension reqConstSize = calculateEffectiveSizes1stPass(colsSizesEffective, rowsSizesEffective);
			if (availWidth < reqConstSize.width || availHeight < reqConstSize.height) {
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
			int availWidth = parent.getWidth() - (parentInsets.left + parentInsets.right);
			int availHeight = parent.getHeight() - (parentInsets.top + parentInsets.bottom);

			// See how much space is for constantly sized components and which
			// for dynamically
			int[] colsSizesEffective = new int[cols];
			int[] rowsSizesEffective = new int[rows];
			Dimension reqConstSize = calculateEffectiveSizes1stPass(colsSizesEffective, rowsSizesEffective);
			if (availWidth < reqConstSize.width || availHeight < reqConstSize.height) {
				// refuse to relayout if not enough space
				return;
			}

			Dimension dynaSpaceAvaialble = new Dimension(availWidth - reqConstSize.width,
					availHeight - reqConstSize.height);
			calculcateEffectiveSizes2ndPass(dynaSpaceAvaialble, colsSizesEffective, rowsSizesEffective);

			// if (log.isDebugEnabled()) {
			// log.debug("Parent insets: " + parentInsets);
			// log.debug("Cols sizes: " + Arrays.toString(colsSizesEffective));
			// log.debug("Rows sizes: " + Arrays.toString(rowsSizesEffective));
			// }

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
				comp.setSize(width, height);
				comp.setBounds(left, top, width, height);

				// log.warn("Comp(" + c.col + ", " + c.row + ", " + c.colspan +
				// ", " + c.rowspan + ") positioned at "
				// + left + "," + top + "," + width + "," + height +
				// ". Actual width: " + comp.getWidth());
			}
		}
	}

	private void calculcateEffectiveSizes2ndPass(Dimension dynaSpaceAvaialble, int[] colsSizesEffective,
			int[] rowsSizesEffective) {
		calculateWeightedSizes(colsSizes, colsSizesTypes, dynaSpaceAvaialble.width, colsSizesEffective, hgap);
		calculateWeightedSizes(rowsSizes, rowsSizesTypes, dynaSpaceAvaialble.height, rowsSizesEffective, vgap);
	}

	/**
	 * Calculate effective cols and rows sizes which hase dynamic sizes
	 * 
	 * @param srcArray
	 * @param sizesTypes
	 * @param dynaSpace
	 *            space which is dynamically available and is to be distributed
	 *            between dynamically sized components
	 * @param gapSize
	 *            gap size for this direction
	 * @return
	 */
	private static void calculateWeightedSizes(int[] srcArray, int[] sizesTypes, int dynaSpace, int[] dstArray,
			int gapSize) {
		double dynaSpaceD = (double) dynaSpace;
		double weightsSumm = 0;
		for (int i = 0; i < srcArray.length; i++) {
			if (sizesTypes[i] == SIZE_TYPE_WEIGHTED) {
				weightsSumm += srcArray[i];
			}
		}

		for (int i = 0; i < srcArray.length; i++) {
			if (sizesTypes[i] == SIZE_TYPE_WEIGHTED) {
				dstArray[i] = (int) (dynaSpaceD * ((double) srcArray[i]) / weightsSumm) + (i > 0 ? gapSize : 0);
			}
		}
	}

	/**
	 * Does two things:
	 * 
	 * - Calculates constant part of the size
	 * 
	 * - Calculates effective final size for cols and rows which has constant
	 * sizes
	 * 
	 * @return total requested constant size required for this layout, including
	 *         layouts
	 */
	private Dimension calculateEffectiveSizes1stPass(int[] effectiveColsSizesToPopulate,
			int[] effectiveRowsSizesToPopulate) {
		int width = getHGapTotalSize();
		int height = getVGapTotalSize();

		for (int idxCol = 0; idxCol < cols; idxCol++) {
			if (colsSizesTypes[idxCol] == SIZE_TYPE_CONSTANT) {
				width += colsSizes[idxCol];
				effectiveColsSizesToPopulate[idxCol] = colsSizes[idxCol] + (idxCol > 0 ? hgap : 0);
			} else if (colsSizesTypes[idxCol] == SIZE_TYPE_ASKCOMPONENT) {
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
					Dimension compSize = getComponentPreferredSize(comp);
					if (compSize.getWidth() > addedWidth) {
						addedWidth = (int) compSize.getWidth();
					}
				}
				if (addedWidth == 0) {
					log.warn("calculateRequestedConstSize(), colIdx " + idxCol
							+ " :: addedWidth is 0, which is not good");
				}
				width += addedWidth;
				effectiveColsSizesToPopulate[idxCol] = addedWidth + (idxCol > 0 ? hgap : 0);
			}

		}

		for (int idxRow = 0; idxRow < rows; idxRow++) {
			if (rowsSizesTypes[idxRow] == SIZE_TYPE_CONSTANT) {
				height += rowsSizes[idxRow];
				effectiveRowsSizesToPopulate[idxRow] = rowsSizes[idxRow] + (idxRow > 0 ? vgap : 0);
			} else if (rowsSizesTypes[idxRow] == SIZE_TYPE_ASKCOMPONENT) {
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
					Dimension compSize = getComponentPreferredSize(comp);
					if (compSize.getHeight() > addedHeight) {
						addedHeight = (int) compSize.getHeight();
					}
				}
				if (addedHeight == 0) {
					log.warn("calculateRequestedConstSize(), rowIdx " + idxRow
							+ " :: addedHeight is 0, which is not good");
				}
				height += addedHeight;
				effectiveRowsSizesToPopulate[idxRow] = addedHeight + (idxRow > 0 ? vgap : 0);
			}
		}

		return new Dimension(width, height);
	}

	@Override
	public float getLayoutAlignmentX(Container target) {
		return 0.5f;
	}

	@Override
	public float getLayoutAlignmentY(Container target) {
		return 0.5f;
	}

}
