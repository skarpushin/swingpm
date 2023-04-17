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
package ru.skarpushin.swingpm.modelprops;

import org.summerb.validation.ValidationError;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import ru.skarpushin.swingpm.collections.FilterPredicate;

public class ValidationErrorsForProperty implements FilterPredicate<ValidationError> {

	private final String fieldToken;

	public ValidationErrorsForProperty(String fieldToken) {
		Preconditions.checkArgument(!Strings.isNullOrEmpty(fieldToken));

		this.fieldToken = fieldToken;
	}

	@Override
	public boolean isSuitable(ValidationError subject) {
		return subject.getFieldToken().equals(fieldToken);
	}
}
