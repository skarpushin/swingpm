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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.skarpushin.swingpm.bindings.BindingContext;
import ru.skarpushin.swingpm.tools.SwingPmSettings;
import ru.skarpushin.swingpm.tools.edt.Edt;

/**
 * Base class for the view
 * 
 * @param <TPM> PresentationModel type
 * @author sergeyk
 */
public abstract class ViewBase<TPM extends PresentationModel> implements View<TPM> {
	protected static Logger log = LogManager.getLogger(ViewBase.class);

	protected TPM pm;
	protected BindingContext bindingContext;

	/**
	 * IMPORTANT: Sub-class must call this method after all properties are
	 * configured.
	 * 
	 * The name of this method comes from Spring, but I don't want to import Spring
	 * in a pom because this seem to be to heavy dependency for this project
	 */
	public void afterPropertiesSet() throws Exception {
		Edt.invokeOnEdtAsync(initComponentsRunnable);
	}

	private Runnable initComponentsRunnable = new Runnable() {
		@Override
		public void run() {
			try {
				log.debug("Initializing components: " + ViewBase.this);
				internalInitComponents();
				log.debug("Initializing components - complete: " + ViewBase.this);
			} catch (Throwable t) {
				log.error("Failed to init components", t);
			}
		}
	};

	@Override
	public boolean isAttached() {
		return pm != null;
	}

	@Override
	public void detach() {
		setPm(null);
	}

	@Override
	public TPM getPm() {
		return pm;
	}

	@Override
	public void setPm(TPM newPm) {
		try {
			Edt.invokeOnEdtAndWait(new SetPmRunnable(newPm));
		} catch (Throwable e) {
			throw new RuntimeException("Failed to set pm", e);
		}
	}

	/**
	 * Helper class which manages PM change for this view
	 * 
	 * @author sergeyk
	 */
	protected class SetPmRunnable implements Runnable {
		private final TPM newPm;

		public SetPmRunnable(TPM newPm) {
			this.newPm = newPm;
		}

		@Override
		public void run() {
			internalSetPm(newPm);
		}
	}

	/**
	 * Called on Swing EDT
	 * 
	 * @param newPm
	 */
	protected void internalSetPm(TPM newPm) {
		if (pm != null) {
			internalUnbindFromPm();
		}

		pm = newPm;

		if (pm != null) {
			log.debug("Binding to PM...: " + ViewBase.this);
			internalBindToPm();
			log.debug("Binding to PM...Complete: " + ViewBase.this);
		}
	}

	@Override
	public void renderTo(Container target, Object constraints) {
		try {
			Edt.invokeOnEdtAndWait(new RenderToRunnable(target, constraints));
		} catch (Throwable t) {
			log.error("Failed to render view", t);
		}
	}

	@Override
	public void renderTo(Container target) {
		try {
			Edt.invokeOnEdtAndWait(new RenderToRunnable(target, null));
		} catch (Throwable t) {
			log.error("Failed to render view", t);
		}
	}

	protected class RenderToRunnable implements Runnable {
		private final Container target;
		private final Object constraints;

		public RenderToRunnable(Container target, Object constraints) {
			this.target = target;
			this.constraints = constraints;
		}

		@Override
		public void run() {
			internalRenderTo(target, constraints);
		}
	}

	@Override
	public void unrender() {
		Edt.invokeOnEdtAndWait(unrenderRunnable);
	}

	private Runnable unrenderRunnable = new Runnable() {
		@Override
		public void run() {
			internalUnrender();
		}
	};

	/**
	 * Most likely will be overridden by subclass in order to bind to PM
	 */
	protected void internalBindToPm() {
		bindingContext = SwingPmSettings.getBindingContextFactory().buildContext();
		pm.registerView(this);
	}

	/**
	 * Most likely will be overridden by subclass in order to un bind to PM
	 */
	protected void internalUnbindFromPm() {
		pm.unregisterView(this);
		bindingContext.unbindAll();
		bindingContext = null;
	}

	protected abstract void internalInitComponents();

	protected abstract void internalRenderTo(Container owner, Object constraints);

	protected abstract void internalUnrender();

}
