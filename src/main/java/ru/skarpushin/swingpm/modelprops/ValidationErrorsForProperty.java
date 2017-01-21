package ru.skarpushin.swingpm.modelprops;

import org.summerb.approaches.validation.ValidationError;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import ru.skarpushin.swingpm.collections.FilterPredicate;

public class ValidationErrorsForProperty implements FilterPredicate<ValidationError> {

	private final String fieldToken;

	public ValidationErrorsForProperty(String fieldToken) {
		Preconditions.checkArgument(Strings.isNullOrEmpty(fieldToken));

		this.fieldToken = fieldToken;
	}

	@Override
	public boolean isSuitable(ValidationError subject) {
		return subject.getFieldToken().equals(fieldToken);
	}
}
