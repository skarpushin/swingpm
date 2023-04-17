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

// TBD: To be exported to swingpm project
public abstract class DialogViewBase<TPM extends PresentationModel> extends ViewBase<TPM> implements HasWindow {
	public static final KeyStroke escapeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
	public static final String dispatchWindowClosingActionMapKey = "ru.skarpushin.swingpm.base.DialogViewBase:WINDOW_CLOSING";

	protected JDialog dialog;
	protected WindowAdapter windowAdapter;
	protected ComponentListener componentAdapter;

	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
		windowAdapter = buildWindowAdapter();
		componentAdapter = buildComponentAdapter();
	}

	@Override
	protected void internalRenderTo(Container owner, Object constraints) {
		Preconditions.checkArgument(owner == null || owner instanceof Window,
				"Target must not be specified or be sub-calss of Window");
		Preconditions.checkState(pm != null, "PM is required for this view");

		if (isDialogMustBeReinitialized(owner, constraints)) {
			tearDownPreviousDialogInstance();
		}

		if (dialog == null) {
			dialog = initDialog((Window) owner, constraints);
			Preconditions.checkState(dialog != null, "Dialog failed to initialize");

			if (componentAdapter != null) {
				dialog.addComponentListener(componentAdapter);
			}
			if (windowAdapter != null) {
				dialog.addWindowListener(windowAdapter);
			}
			installEscapeCloseOperation(dialog, dialog.getRootPane());
			initWindowIcon();
		}

		Window optionalParent = owner instanceof Window ? (Window) owner : null;
		showDialog(optionalParent);
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
	public static void installEscapeCloseOperation(final Window window, JRootPane rootPane) {
		Action dispatchClosing = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent event) {
				window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
			}
		};
		rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeStroke, dispatchWindowClosingActionMapKey);
		rootPane.getActionMap().put(dispatchWindowClosingActionMapKey, dispatchClosing);
	}

	protected void showDialog(Window optionalParent) {
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

	protected boolean isDialogMustBeReinitialized(Container owner, Object constraints) {
		return dialog != null && dialog.getOwner() != owner;
	}

	/**
	 * This is optional. Subclass might want to provide such adapter impl for
	 * handling window closing event
	 */
	protected WindowAdapter buildWindowAdapter() {
		return null;
	}

	protected ComponentAdapter buildComponentAdapter() {
		return new ComponentAdapter() {
			@Override
			public void componentHidden(ComponentEvent e) {
			}

			@Override
			public void componentShown(ComponentEvent e) {
				handleDialogShown();
			}
		};
	}

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

	protected abstract void dispatchWindowCloseEvent(ActionEvent originAction);

	@Override
	public Window getWindow() {
		return dialog;
	}

}
