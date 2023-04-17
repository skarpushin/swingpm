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
package ru.skarpushin.swingpm.base;

import java.awt.Container;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

import com.google.common.base.Preconditions;

public abstract class DialogViewBase<TPM extends PresentationModel> extends ViewBase<TPM> implements HasWindow {
	private static final KeyStroke escapeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
	private static final String dispatchWindowClosingActionMapKey = "ru.skarpushin.swingpm.base.DialogViewBase:WINDOW_CLOSING";

	protected JDialog dialog;

	@Override
	protected void internalRenderTo(Container owner, Object constraints) {
		Preconditions.checkArgument(owner == null || owner instanceof Window,
				"Target must not be specified or be sub-calss of Window");
		Preconditions.checkState(pm != null, "PM is required for this view");

		if (isDislogMustBeReinitialized(owner, constraints)) {
			tearDownPreviousDialogInstance();
		}

		if (dialog == null) {
			dialog = initDialog((Window) owner, constraints);
			Preconditions.checkState(dialog != null, "Dialog failed to initialize");

			dialog.addComponentListener(componentAdapter);
			dialog.addWindowListener(windowAdapter);
			installEscapeCloseOperation(dialog);
			initWindowIcon();
		}

		showDialog();
	}

	protected List<Image> getWindowIcon() {
		return null;
	}

	protected void initWindowIcon() {
		List<Image> images = getWindowIcon();
		if (images == null) {
			return;
		}
		dialog.setIconImages(images);
	}

	@SuppressWarnings("serial")
	public static void installEscapeCloseOperation(final JDialog dialog) {
		Action dispatchClosing = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent event) {
				dialog.dispatchEvent(new WindowEvent(dialog, WindowEvent.WINDOW_CLOSING));
			}
		};
		JRootPane root = dialog.getRootPane();
		root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeStroke, dispatchWindowClosingActionMapKey);
		root.getActionMap().put(dispatchWindowClosingActionMapKey, dispatchClosing);
	}

	protected void showDialog() {
		// NOTE: IMPORTANT: This ismodal dialog, so this call blocks further
		// execution!!!
		dialog.setVisible(true);
	}

	protected void tearDownPreviousDialogInstance() {
		Preconditions.checkState(dialog != null);

		internalUnrender();

		dialog.remove(getRootPanel());
		dialog.dispose();
		dialog = null;
	}

	protected boolean isDislogMustBeReinitialized(Container owner, Object constraints) {
		return dialog != null && dialog.getOwner() != owner;
	}

	private WindowAdapter windowAdapter = new WindowAdapter() {
		@Override
		public void windowClosing(WindowEvent e) {
			super.windowClosing(e);
			if (isAttached()) {
				dispatchWindowCloseEvent();
			}
		};
	};

	private ComponentListener componentAdapter = new ComponentAdapter() {
		@Override
		public void componentHidden(ComponentEvent e) {
		}

		@Override
		public void componentShown(ComponentEvent e) {
			handleDialogShown();
		}
	};

	@Override
	protected void internalUnrender() {
		if (dialog == null || !dialog.isVisible()) {
			return;
		}

		dialog.setVisible(false);
	}

	/**
	 * This called when window is opened. Might be overrided by subclass
	 */
	protected void handleDialogShown() {
	}

	protected abstract JDialog initDialog(Window owner, Object constraints);

	protected abstract JPanel getRootPanel();

	protected abstract void dispatchWindowCloseEvent();

	@Override
	public Window getWindow() {
		return dialog;
	}

}
