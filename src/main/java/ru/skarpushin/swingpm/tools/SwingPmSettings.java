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
package ru.skarpushin.swingpm.tools;

import com.google.common.base.Preconditions;

import ru.skarpushin.swingpm.bindings.BindingContextFactory;
import ru.skarpushin.swingpm.bindings.BindingContextFactoryDefaultImpl;
import ru.skarpushin.swingpm.tools.i18n.MessagesProvider;
import ru.skarpushin.swingpm.tools.i18n.MessagesProviderNoOpImpl;

/**
 * Use this class to integrate SwingPm into your application.
 * 
 * @author sergeyk
 *
 */
public class SwingPmSettings {
	private static MessagesProvider messages = new MessagesProviderNoOpImpl();
	private static BindingContextFactory bindingContextFactory = new BindingContextFactoryDefaultImpl();

	public static MessagesProvider getMessages() {
		return messages;
	}

	public static void setMessages(MessagesProvider messages) {
		Preconditions.checkArgument(messages != null, "Message Provider required");
		SwingPmSettings.messages = messages;
	}

	public static BindingContextFactory getBindingContextFactory() {
		return bindingContextFactory;
	}

	public static void setBindingContextFactory(BindingContextFactory bindingContextFactory) {
		Preconditions.checkArgument(bindingContextFactory != null, "BindingContextFactory required");
		SwingPmSettings.bindingContextFactory = bindingContextFactory;
	}
}
